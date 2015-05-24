///////////////////////////////////////////////////////////////////////
// $Author: rhs041163 $
// // $Id: sunsetSolarDiscoEngine.h,v 1.5 2012/11/28 05:58:27 rhs041163 Exp $
// // $Date: 2012/11/28 05:58:27 $
// // $Revision: 1.5 $
// ///////////////////////////////////////////////////////////////////////

#ifndef __SUNSETSOLAR_DISCO_ENGINE_H__
#define __SUNSETSOLAR_DISCO_ENGINE_H__

#include "sunsetSolarDiscoDataModel.h"
#include "sunsetSolarDiscoThread.h"


#define DATAQ_SIZE   1000

class sunsetSolarDiscoEngine{

public:
    sunsetSolarDiscoEngine();
    ~sunsetSolarDiscoEngine();
    int       configure(sunsetSolarDiscoConfig* configObj);  
    int       startEngine();
    int       stopEngine();
    void      recvQueueLock();
    void      recvQueueUnlock();
    void      recvQueuePush(sunsetSolarDiscoDataObj* obj);
    bool      isRecvQueueEmpty();
    sunsetSolarDiscoDataObj*
              readDiscoData();

private:
    dataQueue<sunsetSolarDiscoDataObj*>  *txqueue; // This Q is used for Request 
    dataQueue<sunsetSolarDiscoDataObj*>  *recvqueue; // This Q is used for Response
    pthread_mutex_t                      txqueue_lock;
    pthread_mutex_t                      recvqueue_lock;
    sunsetSolarDiscoThreadBase           *thread;
};



#endif /* __SUNSETSOLAR_DISCO_ENGINE_H__ */
