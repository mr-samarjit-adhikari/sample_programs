/**
 * This is demonstration of RW mutex implementation
 * without use of pthread_rwlock_wrlock/pthread_rwlock_rdlock
 * library call.
 * Reader's priority
 */

#include <stdio.h>
#include <pthread.h>
#include <semaphore.h>


#define     MAX_READ_THREAD_COUNT   10
#define     MAX_WRITE_THREAD_COUNT  1


/**
 * An RW mutex stricture .
 */
struct rwmutex{
    /* Interface functions */
    int (*read_lock)(struct rwmutex*);
    int (*write_lock)(struct rwmutex*);
    int (*read_unlock)(struct rwmutex*);
    int (*write_unlock)(struct rwmutex*);

    /* Private members, shouldn't be accessed directly */
    unsigned int        reader_count,writer_count;
    pthread_mutex_t     mutex_lock;
    sem_t               reader_lock;
    sem_t               writer_lock;
};
typedef struct rwmutex rwmutex_t;

/* Static interface functions */
static int rwmutex_write_lock(rwmutex_t *amutex){

    
}

static int rwmutex_write_unlock(rwmutex_t* amutex){

    int     ret = 0;

    if((ret = sem_post(&amutex->reader_lock))<0){
        perror("rwmutex_write_unlock:sem_post failed.");
        return ret;
    }

    if((ret = pthread_mutex_unlock(&amutex->mutex_lock))<0){
        perror("rwmutex_write_unlock:pthread_mutex_unlock failed.");
        return ret;
    }
}

static int rwmutex_read_lock(rwmutex_t* amutex){

    int      ret = 0;

    if((ret = sem_wait(&amutex->reader_lock))<0){
        perror("rwmutex_read_lock: sem_wait failed");
    }

    return ret;
}

static int rwmutex_read_unlock(rwmutex_t* amutex){

    int      ret = 0;

    if((ret = sem_post(&amutex->reader_lock))<0){
        perror("rwmutex_read_unlock: sem_wait failed");
    }

    return ret;
}

/* Interface Function definitions */
int rwmutex_init(rwmutex_t*  amutex){

    int     ret = 0;
    
    /* Initialize all internal variables .*/
    if((ret = pthread_mutex_init(&(amutex->mutex_lock),NULL))<0){
        perror("Mutex initialixation failed.");
        return ret;
    }

    if((ret = sem_init(&(amutex->reader_lock),0,MAX_READ_THREAD_COUNT))<0){
        perror("sem initialization failed.");
        return ret;
    }

    if((ret = sem_init(&(amutex->reader_lock),0,MAX_WRITE_THREAD_COUNT))<0){
        perror("sem initialization failed.");
        return ret;
    }


    /* initialize all interface functions */
    amutex->read_lock = rwmutex_read_lock;
    amutex->write_lock = rwmutex_write_lock;
    amutex->read_unlock = rwmutex_read_unlock;
    amutex->write_unlock = rwmutex_write_unlock;

    /* Initialize variables */
    amutex->reader_count = 0;
    amutex->writer_count = 0;
}




main()
{
    int             count = 0;
    int             ind   = 0;
    int             ret   = 0;
    int            status = 0;
    pthread_t   thread_list[MAX_THREAD_COUNT];
    int         thread_list_int[MAX_THREAD_COUNT];

    /* Initialize the sem_t variable */
    sem_init(&job_queue_count,0,0);
    job_queue = NULL;
    
    for(count=0;count<MAX_THREAD_COUNT/3;count++){
        thread_list_int[count] = count;
        ret = pthread_create(&thread_list[count],NULL,
                     thread_callback_dequeue,&thread_list_int[count]);
        if(ret!=0){
            fprintf(stderr,"[%d] thread creation failed...\n",count);
            perror("Exiting...");
        }   
    }
    
    /*      Create the thread for enqueue.      */
    for(;count<MAX_THREAD_COUNT;count++){
        thread_list_int[count] = count;
        ret =  pthread_create(&thread_list[count],NULL,
                      thread_callback_enqueue,&thread_list_int[count]);
        if(ret!=0){
            fprintf(stderr,"[%d] thread creation failed...\n",count);
            perror("Exiting...");
        }
    }
}
