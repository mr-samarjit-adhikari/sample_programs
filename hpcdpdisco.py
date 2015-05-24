
#
# Version: POC
# Author: samarjit.adhikari@hp.com
# Dependencies: pysnmp: http://pypi.python.org/packages/source/p/pysnmp/
#               pyasn1: http://pypi.python.org/packages/source/p/pyasn1/
#
#
#
# Usage:
#         ./hpcdpdisco.py <seedname>
#

import sys,getopt,os
import logging
import struct
from pysnmp.entity.rfc3413.oneliner import cmdgen
from cdpgraph import cdpgraph

logger = logging.getLogger("cdpwalker")
logger.setLevel(logging.INFO)
ch = logging.StreamHandler()
formatter = logging.Formatter("%(asctime)s - %(name)s - %(levelname)s - %(message)s")
ch.setFormatter(formatter)
logger.addHandler(ch)

OID_SYSNAME = '1.3.6.1.2.1.1.5.0'
OID_CDP_CACHE_ENTRY = '1.3.6.1.4.1.9.9.23.1.2.1.1'
OID_CDP_CACHE_DEVICEID = '1.3.6.1.4.1.9.9.23.1.2.1.1.6.'
OID_CDP_CACHE_DEVICEPORT = '1.3.6.1.4.1.9.9.23.1.2.1.1.7.'
OID_CDP_CACHE_ADDRESS = '1.3.6.1.4.1.9.9.23.1.2.1.1.4.'


# (host1, host1_if, host2, host2_if)
NEIGHBOR_TABLE = []
DEVICES = {}

class SnmpSession(object):
    """SNMP Session object"""

    def __init__(self,host,comm_str):
        self.host = host
        self.port = 161
        self.community = comm_str
        self.version = "2c"

    def get_config(self):
        if self.version == "1":
            return  cmdgen.CommunityData('test-agent', self.community, 0),

        elif self.version == "2c":
            return cmdgen.CommunityData('test-agent', self.community)

        elif self.version == "3":
            return cmdgen.UsmUserData('test-user', 'authkey1', 'privkey1'),

    def oidstr_to_tuple(self, s):
        """ FIXME remove trailing dot if there is one"""

        return tuple([int(n) for n in s.split(".")])

    def snmp_get(self, oid):
        r = ()

        oid = self.oidstr_to_tuple(oid)

        snmp_config = self.get_config()

        errorIndication, errorStatus, \
            errorIndex, varBinds = cmdgen.CommandGenerator().getCmd(
            snmp_config, cmdgen.UdpTransportTarget((self.host, self.port)), oid)

        if errorIndication:
            print errorIndication
            print errorStatus
            print errorIndex
        else:
            if errorStatus:
                print '%s at %s\n' % (
                    errorStatus.prettyPrint(), varBinds[int(errorIndex)-1])
            else:
                for name, val in varBinds:
                    return (name.prettyPrint(), val.prettyPrint())

    def snmp_getnext(self, oid):
        r = []

        oid = self.oidstr_to_tuple(oid)
        snmp_config = self.get_config()

        errorIndication, errorStatus, errorIndex, \
            varBindTable = cmdgen.CommandGenerator().nextCmd(
            snmp_config, cmdgen.UdpTransportTarget((self.host, self.port)), oid)

        if errorIndication:
            print errorIndication
            print errorStatus
            print errorIndex
        else:
            if errorStatus:
                print '%s at %s\n' % (
                    errorStatus.prettyPrint(), varBindTable[-1][int(errorIndex)-1])
            else:
                for varBindTableRow in varBindTable:
                    for name, val in varBindTableRow:
                        r.append((name.prettyPrint(), val.prettyPrint()))

        return r



class CdpDevice(object):
    deviceid = ""
    deviceport = ""
    address = ""


def get_cache_ifindex(snmpoid):
    return int(snmpoid.split(".")[-2])


def get_cdp_neighbors(snmpSession,host):
    neighbors = {}
    neighbor_relations = []
    hostname = ""

    logger.info("processing host: %s" % host)

    snmp = snmpSession;
    resp = snmp.snmp_getnext(OID_CDP_CACHE_ENTRY)
    if resp == []:
        logger.warn("failed to query %s by snmp" % host)
        return [], []

    for oid,val in resp:
        snmpoid, value = oid, val
        ifindex = get_cache_ifindex(snmpoid)

        if not ifindex in neighbors:
            neighbors[ifindex] = CdpDevice()

        if snmpoid.startswith(OID_CDP_CACHE_ADDRESS):
            #import pdb;pdb.set_trace();
            neighbors[ifindex].address = "%i.%i.%i.%i" % (\
                                         int(value[2:3],16),int(value[3:4],16),
                                         int(value[4:5],16),int(value[5:6],16))
        elif snmpoid.startswith(OID_CDP_CACHE_DEVICEID):
            neighbors[ifindex].deviceid = value
        elif snmpoid.startswith(OID_CDP_CACHE_DEVICEPORT):
            if hostname == "":
                hostname = snmp.snmp_get(OID_SYSNAME)[1]
            # ifDescr
            ifname = snmp.snmp_get("1.3.6.1.2.1.2.2.1.2.%i" % ifindex)[1]
            deviceport = value
            var1 = (hostname, ifname, neighbors[ifindex].deviceid, deviceport)
            var2 = (neighbors[ifindex].deviceid, deviceport, hostname, ifname)
            if not var1 in neighbor_relations and not var2 in neighbor_relations:
                neighbor_relations.append(var1)

    return [neighbors[neigh] for neigh in neighbors], neighbor_relations


def print_relations(relations, filename=None):
    """
    Print it in tabular form for now
    """
    print "%30s %30s %30s %30s"%('LocalHost','LocalPort','RemotePort','RemoteHost')
    for relrow in relations:
        print "%30s %30s --> %30s %30s"%(relrow[0],relrow[1],relrow[3],relrow[2])
#    mycdpgraph = cdpgraph(relations)
#    mycdpgraph.draw("example.svg")
#    os.system("firefox example.svg") 


def merge_relations(relations, relations2):
    for relation in relations2:
        relation_var2 = (relation[2], relation[3], relation[0], relation[1])
        if not relation in relations and not relation_var2 in relations:
            relations.append(relation)

    return relations

def usage():
    print "Usage: %s -c <community string>  <node> [... <nodes>]" \
          %(sys.argv[0])
    return

if __name__ == "__main__":
    """
        MAIN Function
    """
    try:
	opts,args = getopt.getopt(sys.argv[1:],"c:");
    except getopt.GetoptError, err:
        print str(err)
        usage()
        sys.exit(2);

    for op,val in opts:
	if op == "-c":
            commstr = val

    #args holds list of hosts
    if (len(args) == 0):
        print "Error: <node> required!"
        usage();
        sys.exit(2);

    relations = []
 
    for host in args:
	session = SnmpSession(host,commstr)
	dev, rel = get_cdp_neighbors(session,host)
	relations = merge_relations(relations, rel)
	logger.info("host %s done" % host)

    print_relations(relations)

