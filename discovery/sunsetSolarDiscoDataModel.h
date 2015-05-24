///////////////////////////////////////////////////////////////////////
// $Author: rhs041163 $
// $Id: sunsetSolarDiscoDataModel.h,v 1.5 2012/11/27 13:02:52 rhs041163 Exp $
// $Date: 2012/11/27 13:02:52 $
// $Revision: 1.5 $
// ///////////////////////////////////////////////////////////////////////

#ifndef __SUNSETSOLAR_DISCO_DATA_MODEL_H__
#define __SUNSETSOLAR_DISCO_DATA_MODEL_H__

#include <string>
#include <iostream>
using namespace std;

class sunsetSolarDiscoConfig{
public:
    sunsetSolarDiscoConfig();
    ~sunsetSolarDiscoConfig(); //populate later
};

class sunsetSolarDiscoDataObj{
public:
    sunsetSolarDiscoDataObj(string &name,string &value);
    sunsetSolarDiscoDataObj(char* name,char* value);
    ~sunsetSolarDiscoDataObj();
    string& getName(){ return name;}
    string& getValue(){ return value;}
private:
    string  name;
    string  value;
};

template <class TYPE> class dataQueue{
public:
    dataQueue<TYPE>(unsigned int size);
    ~dataQueue<TYPE>();
    bool isEmpty(){ return front==tail;}
    void push(TYPE item);
    TYPE pop();
    void printQueue();

private:
    unsigned int size;
    bool         queueFull;
    class CBuffer{
        public:
            TYPE item;
            CBuffer *next;
    };
    CBuffer*   front;
    CBuffer*   tail;
};

template <class TYPE >
dataQueue<TYPE>::dataQueue(unsigned int sz):size(sz),queueFull(false)
{
    unsigned int index = 0;

    front = tail = NULL;
    CBuffer* next = front;
    CBuffer* prev = front;

    for(index=0;index<sz;index++){
        next = new CBuffer();
        next->next = front;
        if(index==0){
            front = next;
        }
        else{
            prev->next = next;
        }
        prev = next;
    }
    // Set the tail to front as well
    tail = front;
}

template <class TYPE>
dataQueue<TYPE>::~dataQueue(){
    // Nothing to be deleted
}

template<class TYPE>
void dataQueue<TYPE>::push(TYPE item)
{
    if(false == this->queueFull){
        tail->item = item;
    }
    else{
        std::cout<<"Queue is Full!. Ignoring "<<item<<endl;
        return;
    }

    if(NULL == tail->next || tail->next == front){ // Queue is Full
        this->queueFull = true;
    }
    else{
        tail = tail->next;
    }
}

template <class TYPE>
TYPE dataQueue<TYPE>::pop()
{
    TYPE   item;

    if(front == tail) cout<<"Queue is empty."<<endl;
    else{
       item  = front->item;
       front = front->next;
       this->queueFull = false;
        
       return item;
    }

}

template <class TYPE>
void dataQueue<TYPE>::printQueue()
{
    unsigned int id = 0;
    CBuffer* buff = front;

    for(buff=front;buff!=tail;buff=buff->next){
         cout<<(buff->item)<<" ";
    }
    cout<<(buff->item)<<" "<<endl;
}


#endif /* __SUNSETSOLAR_DISCO_DATA_MODEL_H__ */
