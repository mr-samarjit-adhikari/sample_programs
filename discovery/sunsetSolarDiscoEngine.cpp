///////////////////////////////////////////////////////////////////////
// $Author: rhs041163 $
// // $Id: sunsetSolarDiscoEngine.cpp,v 1.6 2012/11/28 05:58:27 rhs041163 Exp $
// // $Date: 2012/11/28 05:58:27 $
// // $Revision: 1.6 $
// ///////////////////////////////////////////////////////////////////////

#include <stdio.h>
#include <sys/types.h>
#include <errno.h>
#include <unistd.h>
#include <string.h>

#include "sunsetSolarDiscoDataModel.h"
#include "sunsetSolarDiscoEngine.h"


#define PATH_PROCNET_ARP     "/proc/net/arp"

sunsetSolarDiscoEngine::sunsetSolarDiscoEngine()
{
    this->txqueue = new dataQueue<sunsetSolarDiscoDataObj*>(DATAQ_SIZE);
    this->recvqueue = new dataQueue<sunsetSolarDiscoDataObj*>(DATAQ_SIZE);
    if(txqueue == NULL|| recvqueue == NULL){
        fprintf(stderr,"%s:%d Memory allocation failed!\n",
                __FILE__,__LINE__);
    }
    else{
        pthread_mutex_init(&(this->txqueue_lock),NULL);
        pthread_mutex_init(&(this->recvqueue_lock),NULL);
    }
}

sunsetSolarDiscoEngine::~sunsetSolarDiscoEngine()
{
    pthread_mutex_destroy(&(this->txqueue_lock));
    pthread_mutex_destroy(&(this->recvqueue_lock));

    delete this->txqueue;
    delete this->recvqueue;
}

//
// @param: configObj  - configuration object from which txqueue 
//                      will be populated
// @return: int       - Return -1 on error else 0
//
int
sunsetSolarDiscoEngine::configure(sunsetSolarDiscoConfig* configObj)
{
    char      line[200] = {'\0'};
    FILE*     fp        = NULL;
    char      ip[100]   = {'\0'};
    char      hwa[100]  = {'\0'};
    char      mask[100] = {'\0'};
    char      dev[100]  = {'\0'};
    int       num       = 0;
    int       type      = 0;
    int       flags     = 0;
    

    if(geteuid() != 0){
        fprintf(stderr,"Effective user does not have enough permission"
                       " to execute!\n");
        fflush(stderr);
        return -1;
    }

    /* read kernel proc entry for ARP cache */
    if((fp=fopen(PATH_PROCNET_ARP,"r"))==NULL){
        fprintf(stderr,"Not able to open path %s.%s\n",PATH_PROCNET_ARP,
                strerror(errno));
        fflush(stderr);
        return (-1);
    }
    /* Skip the header .*/
    if((fgets(line,sizeof(line),fp)) != (char*)NULL){
        for(;(fgets(line,sizeof(line),fp));){
            num = sscanf(line, "%s 0x%x 0x%x %100s %100s %100s\n",
                         ip, &type, &flags, hwa, mask, dev );
            if(num<4){
                fprintf(stderr,"Not enough enntry in proc \n");
                fflush(stderr);
                continue;
            }
            //We are concern about only Ether type
            if(type == 0x1){
                //Put it in txqueue
                pthread_mutex_lock(&(this->txqueue_lock));
                this->txqueue->push(new sunsetSolarDiscoDataObj(ip,hwa));
                pthread_mutex_unlock(&(this->txqueue_lock));
            }
        }
    }
   
    fclose(fp); 
    /* Just to remove compilation warning */
    configObj = configObj;
    return 0;
}

void*
thread_dispatcher(void* args){
    /* This displatcher assumes that args object is intanceof 
     * sunsetSolarDiscoThreadBase class */
    sunsetSolarDiscoThreadBase*  threadobj = (sunsetSolarDiscoThreadBase*)args;
    pthread_t   selfId = pthread_self();
    int         ret = 0;

    threadobj->setThreadId(selfId);
    ret = pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL);
    if (ret != 0)
        perror("pthread_setcancelstate Error! \n");
    threadobj->run();
    
    return NULL;
}


int
sunsetSolarDiscoEngine::startEngine()
{
    // Here i shall start a new thread and return 
    pthread_t    thrId;

    this->thread = new sunsetSolarDiscoWorkerThread();
    if(this->thread == NULL){
        fprintf(stderr,"%s: %d Memory Allocation failed\n",__FILE__,__LINE__);
        fflush(stderr);
        return (-1);
    }
    (dynamic_cast<sunsetSolarDiscoWorkerThread*>(this->thread))->setEngine(this);
    if(pthread_create(&thrId,NULL,&(thread_dispatcher),(void*)(this->thread))<0){
        perror(" pthread_create() failed \n");
        return (-1);
    }
               
    return 0; 
}

int
sunsetSolarDiscoEngine::stopEngine()
{
    pthread_t   thrId = this->thread->getThreadId();
    pthread_cancel(thrId);
    
    return 0;
}

sunsetSolarDiscoDataObj*
sunsetSolarDiscoEngine::readDiscoData()
{
    sunsetSolarDiscoDataObj*  data = NULL;

    pthread_mutex_lock(& this->recvqueue_lock);
    if(! this->recvqueue->isEmpty()){
        data = this->recvqueue->pop();
    }
    pthread_mutex_unlock(& this->recvqueue_lock);

    return data;
}

void
sunsetSolarDiscoEngine::recvQueueLock()
{
    pthread_mutex_lock(& this->recvqueue_lock);
}

void
sunsetSolarDiscoEngine::recvQueueUnlock()
{
    pthread_mutex_unlock(& this->recvqueue_lock);
}

void
sunsetSolarDiscoEngine::recvQueuePush(sunsetSolarDiscoDataObj* obj)
{
    this->recvqueue->push(obj);
}

bool
sunsetSolarDiscoEngine::isRecvQueueEmpty()
{
    bool  isEmpty = false;
    this->recvQueueLock();
    isEmpty = this->recvqueue->isEmpty();
    this->recvQueueUnlock();

    return isEmpty;
}
