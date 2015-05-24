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

#define   IP_ALEN  4
#define   IFNAME   "eth0"

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

void prepare_slladdr(int ps,struct sockaddr_ll* sockaddr,char* mymac){
    // Populate the sockaddr with my eth0 interface
    // HW address
    struct ifreq  ifr;
    memcpy(&(ifr.ifr_name),IFNAME,sizeof(IFNAME));
    if((ioctl(ps,SIOCGIFHWADDR,&ifr))<0){
        perror("ioctl(SIOCGIFHWADDR) failed!");
        exit(1);
    }
    // Now populate the sockaddr hw addr
    memcpy(sockaddr->sll_addr,&(ifr.ifr_hwaddr.sa_data),ETH_ALEN);
    memcpy(mymac,&(ifr.ifr_hwaddr.sa_data),ETH_ALEN);
    
    // Get the interface index and populate
    if((ioctl(ps,SIOCGIFINDEX,&ifr))<0){
        perror("ioctl(SIOCGIFINDEX) failed!");
        exit(1);
    }
    sockaddr->sll_ifindex = ifr.ifr_ifindex;
    sockaddr->sll_protocol = htons(ETH_P_ARP);
}

void prepare_ether_packet(ether_packet_linux_t *eth_packet,char* mymac)
{
    struct in_addr  arbitary_ip;
    struct in_addr  my_ip;

    inet_aton("1.2.3.4",&arbitary_ip);
    inet_aton("10.0.2.15",&my_ip);

    memset(eth_packet->dest,0xFF,ETH_ALEN); // Broadcast
    memcpy(eth_packet->src,mymac,ETH_ALEN);
    
    eth_packet->eth_protocol = htons(0x806); //ARP
    eth_packet->arp_packet.htype = htons(0x1);
    eth_packet->arp_packet.ptype = htons(0x800);
    eth_packet->arp_packet.hlen  = 0x6;
    eth_packet->arp_packet.plen  = 0x4;
    eth_packet->arp_packet.op_code = htons(0x1); // ARP request
    memcpy(eth_packet->arp_packet.sender_mac,mymac,ETH_ALEN);
    memcpy(eth_packet->arp_packet.sender_ip,&my_ip,IP_ALEN);
    memcpy(eth_packet->arp_packet.target_ip,&arbitary_ip,IP_ALEN);
    //target mac is not filled out
}

main()
{
	int                 packet_socket = -1;
	char                pbuff[255]={0xA};
	unsigned int        pbuff_len = 255;
	unsigned int        index = 0;
	struct sockaddr_ll  *sock_addr = NULL;
    unsigned char       mymac[ETH_ALEN];
    ether_packet_linux_t eth_packet;
    socklen_t           sock_addr_len = sizeof(struct sockaddr_ll);

    /* socket() call has protocol field which is used during
     * receiving packets from network, this is directly mapped
     * either protocol field in IP header or protocol field in
     * ether header based on the opening socket in a level like 
     * Application level,link level, tcp level etc
     * */
	if((packet_socket=socket(AF_PACKET,SOCK_RAW,ETH_P_ARP))<0){
		perror("Socket creation error!");
		exit(1);
	}

	sock_addr = (struct sockaddr_ll*)calloc(1,sizeof(struct sockaddr_ll));
	if(NULL == sock_addr){
		perror("sock_addr allocation failed!");
		exit(1);
	}

	//Prepare sockaddr_ll for sending
	sock_addr->sll_family = AF_PACKET;
    prepare_slladdr(packet_socket,sock_addr,mymac);
    /* Now bind the sock_addr to get ARP reply */
    if(bind(packet_socket,(struct sockaddr*)sock_addr,sock_addr_len)<0){
        perror("Bind Error!");
    }

    prepare_ether_packet(&eth_packet,mymac);

	if((sendto(packet_socket,&eth_packet,sizeof(eth_packet),0,
		  (struct sockaddr*)sock_addr,sizeof(struct sockaddr_ll)))<0){
		perror("Send to failed!");
		exit(1);
	}

    while(1){
        /* Going to recev the reply */

        if((recvfrom(packet_socket,&eth_packet,sizeof(eth_packet),0,
                    (struct sockaddr*)sock_addr,&sock_addr_len))<0){
            perror("recvfrom error!");
        }
        else{
            printf("ARP %s from %02x:%02x:%02x:%02x:%02x:%02x\n",
                    (eth_packet.arp_packet.op_code==htons(0x1))?"REQUEST":"REPLY",
                    eth_packet.arp_packet.sender_mac[0],eth_packet.arp_packet.sender_mac[1],
                    eth_packet.arp_packet.sender_mac[2],eth_packet.arp_packet.sender_mac[3],
                    eth_packet.arp_packet.sender_mac[4],eth_packet.arp_packet.sender_mac[5]);   
        }
    }

	return 0;
}
