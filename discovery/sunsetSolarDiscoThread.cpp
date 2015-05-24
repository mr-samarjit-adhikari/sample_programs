///////////////////////////////////////////////////////////////////////
// $Author: rhs041163 $
// $Id: sunsetSolarDiscoThread.cpp,v 1.4 2012/11/28 05:58:27 rhs041163 Exp $
// $Date: 2012/11/28 05:58:27 $
// $Revision: 1.4 $
///////////////////////////////////////////////////////////////////////
//

#include <sys/socket.h>
#include <sys/ioctl.h>
#include <netpacket/packet.h>
#include <net/ethernet.h>
#include <net/if.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <stdio.h>
#include <pthread.h>

#include "sunsetSolarDiscoDataModel.h"
#include "sunsetSolarDiscoEngine.h"
#include "sunsetSolarDiscoThread.h"

sunsetSolarDiscoWorkerThread::sunsetSolarDiscoWorkerThread()
{
}

sunsetSolarDiscoWorkerThread::~sunsetSolarDiscoWorkerThread()
{}

void
sunsetSolarDiscoWorkerThread::run()
{
#define   IP_ALEN  4
#define   IFNAME   "eth0"
//#define   IFNAME   "wlan0"

struct arp_packet_linux{
    unsigned short htype;
    unsigned short ptype;
    unsigned char  hlen;
    unsigned char  plen;
    unsigned short op_code;
    unsigned char  sender_mac[ETH_ALEN];
    unsigned char  sender_ip[IP_ALEN];
    unsigned char  target_mac[ETH_ALEN];
    unsigned char  target_ip[IP_ALEN];
};
typedef struct arp_packet_linux arp_packet_linux_t;

struct ether_packet_linux{
    unsigned char  dest[ETH_ALEN];
    unsigned char  src[ETH_ALEN];
    unsigned short eth_protocol;
    arp_packet_linux_t arp_packet;
};
typedef struct ether_packet_linux ether_packet_linux_t;

    /* open an ARP Listener */

    int                 packet_socket = -1;
    char                discovered_mac[50];
    char                discovered_ip[50];
    struct sockaddr_ll  *sock_addr = NULL;
    unsigned char       mymac[ETH_ALEN];
    ether_packet_linux_t eth_packet;
    struct ifreq  ifr;
    socklen_t           sock_addr_len = sizeof(struct sockaddr_ll);


    /* socket() call has protocol field which is used during
     * receiving packets from network, this is directly mapped
     * either protocol field in IP header or protocol field in
     * ether header based on the opening socket in a level like 
     * Application level,link level, tcp level etc
     * * */
    if((packet_socket=socket(AF_PACKET,SOCK_RAW,ETH_P_ARP))<0){
        perror("Socket creation error!");
        exit(1);
    }

    sock_addr = (struct sockaddr_ll*)calloc(1,sizeof(struct sockaddr_ll));
    if(NULL == sock_addr){
        perror("sock_addr allocation failed!");
        exit(1);
    }

    //Prepare sockaddr_ll 
    memcpy(&(ifr.ifr_name),IFNAME,sizeof(IFNAME));
    if((ioctl(packet_socket,SIOCGIFHWADDR,&ifr))<0){
        perror("ioctl(SIOCGIFHWADDR) failed!");
        exit(1);
    }
    memcpy(sock_addr->sll_addr,&(ifr.ifr_hwaddr.sa_data),ETH_ALEN);
    memcpy(mymac,&(ifr.ifr_hwaddr.sa_data),ETH_ALEN);

    if((ioctl(packet_socket,SIOCGIFINDEX,&ifr))<0){
        perror("ioctl(SIOCGIFINDEX) failed!");
        exit(1);
    }
    sock_addr->sll_family = AF_PACKET;
    sock_addr->sll_ifindex = ifr.ifr_ifindex;
    sock_addr->sll_protocol = htons(ETH_P_ARP);
   
    if(bind(packet_socket,(struct sockaddr*)sock_addr,sock_addr_len)<0){
        perror("Bind Error!");
    }
 
   while(1){

        if((recvfrom(packet_socket,&eth_packet,sizeof(eth_packet),0,
                    (struct sockaddr*)sock_addr,&sock_addr_len))<0){
            perror("recvfrom error!");
        }
        else{
           /*
            printf("ARP %s from %02x:%02x:%02x:%02x:%02x:%02x IP- [%d.%d.%d.%d]\n",
                    (eth_packet.arp_packet.op_code==htons(0x1))?"REQUEST":"REPLY",
                    eth_packet.arp_packet.sender_mac[0],eth_packet.arp_packet.sender_mac[1],
                    eth_packet.arp_packet.sender_mac[2],eth_packet.arp_packet.sender_mac[3],
                    eth_packet.arp_packet.sender_mac[4],eth_packet.arp_packet.sender_mac[5],
                    eth_packet.arp_packet.sender_ip[0] & 0xFF,
                    eth_packet.arp_packet.sender_ip[1] & 0xFF,
                    eth_packet.arp_packet.sender_ip[2] & 0xFF,
                    eth_packet.arp_packet.sender_ip[3] & 0xFF);*/

            sprintf(discovered_mac,"%02x:%02x:%02x:%02x:%02x:%02x",
                    eth_packet.arp_packet.sender_mac[0],eth_packet.arp_packet.sender_mac[1],
                    eth_packet.arp_packet.sender_mac[2],eth_packet.arp_packet.sender_mac[3],
                    eth_packet.arp_packet.sender_mac[4],eth_packet.arp_packet.sender_mac[5]);

            sprintf(discovered_ip,"%d.%d.%d.%d",
                    eth_packet.arp_packet.sender_ip[0] & 0xFF,
                    eth_packet.arp_packet.sender_ip[1] & 0xFF,
                    eth_packet.arp_packet.sender_ip[2] & 0xFF,
                    eth_packet.arp_packet.sender_ip[3] & 0xFF);

            
            this->discoEngine->recvQueueLock();
            this->discoEngine->recvQueuePush
            (new sunsetSolarDiscoDataObj(discovered_mac,discovered_ip));
            this->discoEngine->recvQueueUnlock();
        }
    }
}


