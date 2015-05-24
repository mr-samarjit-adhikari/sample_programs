#include <iostream>

using namespace std;

template <class T>
class Queue{
    public:
        Queue<T>(unsigned int size);
        ~Queue<T>();
        void push(T item);
        T pop();
        void printQueue();
    private:
        unsigned int size;
        bool     queueFull;

        class CBuffer{
            public:
                T item;
                CBuffer* next;
        };
        CBuffer* front;
        CBuffer* tail;

};

template <class T>
Queue<T>::Queue(unsigned int sz):size(sz),queueFull(false)
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

template <class T>
Queue<T>::~Queue(){
    //delete this->buffer;
}

template<class T>
void Queue<T>::push(T item)
{
    if(false == this->queueFull){
        tail->item = item;
    }
    else{
        cout<<"Queue is Full!. Ignoring "<<item<<endl;
        return;
    }

    if(NULL == tail->next || tail->next == front){ // Queue is Full
        this->queueFull = true;
    }
    else{
        tail = tail->next;
    }
}

template <class T>
T Queue<T>::pop()
{
    T   item;

    if(front == tail) cout<<"Queue is empty."<<endl;
    else{
       item  = front->item;
       front = front->next;
        this->queueFull = false;
    }

    return item;
}

template <class T>
void Queue<T>::printQueue()
{
    unsigned int id = 0;
    CBuffer* buff = front;

    for(buff=front;buff!=tail;buff=buff->next){
         cout<<(buff->item)<<" ";
    }
    cout<<(buff->item)<<" "<<endl;
}


main()
{
    Queue<int> myQueue(2);
    myQueue.push(1);
    myQueue.push(2);
    myQueue.push(3);
    myQueue.printQueue();

    cout<<"Poped item "<<myQueue.pop();
    cout<<"Poped item"<<myQueue.pop();
    myQueue.pop();
}
