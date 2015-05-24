///////////////////////////////////////////////////////////////////////
// $Author: rhs041163 $
// // $Id: sunsetSolarDiscoThread.h,v 1.3 2012/11/27 08:49:17 rhs041163 Exp $
// // $Date: 2012/11/27 08:49:17 $
// // $Revision: 1.3 $
// ///////////////////////////////////////////////////////////////////////

#ifndef __SUNSETSOLAR_DISCO_THREAD_H__
#define __SUNSETSOLAR_DISCO_THREAD_H__

#include <pthread.h>

class sunsetSolarDiscoEngine;
class sunsetSolarDiscoThreadBase{
public:
    sunsetSolarDiscoThreadBase(){};
    ~sunsetSolarDiscoThreadBase(){};
    pthread_t      getThreadId(){return this->threadId;}
    void setThreadId(pthread_t id){ threadId = id;}
    virtual void run(){}
private:
    pthread_t      threadId;
};

class sunsetSolarDiscoWorkerThread: public sunsetSolarDiscoThreadBase
{
public:
    sunsetSolarDiscoWorkerThread();
    ~sunsetSolarDiscoWorkerThread();
    void setEngine(sunsetSolarDiscoEngine* engn){ this->discoEngine = engn;}
    virtual void run();
private:
    sunsetSolarDiscoEngine*     discoEngine;
};

#endif /*__SUNSETSOLAR_DISCO_THREAD_H__*/
