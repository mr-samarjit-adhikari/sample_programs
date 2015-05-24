/**

This is sample program from http://gcc.gnu.org/bugzilla/show_bug.cgi?id=50569
Need modification for generating sigbus error

*/

typedef signed char __int8_t;
typedef unsigned char __uint8_t;
typedef short int __int16_t;
typedef unsigned short int __uint16_t;
typedef int __int32_t;
typedef unsigned int __uint32_t;
typedef long long int __int64_t;
typedef unsigned long long int __uint64_t;
typedef char * __caddr_t;
typedef int time_t;
typedef int clockid_t;
typedef int timer_t;
typedef int suseconds_t;
typedef unsigned long size_t;
typedef long ssize_t;

typedef struct sgElement_s
{
    void* buf;
    __uint32_t len;
} sgElement_t;

typedef struct
{
    sgElement_t *m_iovecs;
    char * m_start;
    __uint32_t unused;
    __uint32_t m_freeSpace;
    __uint32_t m_size;
    __uint32_t m_numIovecs;
    __uint16_t m_last;
    __uint32_t m_allDone;
    char * m_panicString;
} eqllogQInfo_t;
eqllogQInfo_t g_eqllogQInfo;
eqllogQInfo_t *eqllogQInfo = &g_eqllogQInfo;
sgElement_t iovecs[4];

typedef struct
{
    __uint64_t low;
    __uint64_t hi;
} uuid_t;

typedef enum
{
    eqllogPayloadSent=0,
    eqllogPayloadError,
    eqllogPayloadDone,
    eqllogPayloadAcked,
    eqllogPayloadDump,
    eqllogPayloadPanic,
    eqllogPayloadSecondaryFlush,
    eqllogPayloadLAST
} eqllogPayloadState_t;

struct timeval {
 time_t tv_sec;
 suseconds_t tv_usec;
};

typedef struct
{
    __uint32_t m_cksum __attribute__((packed));
    __uint32_t m_numEvents __attribute__((packed));
    __uint32_t m_name_unused __attribute__((packed));
    uuid_t m_uuid __attribute__((packed));
    __uint64_t m_index __attribute__((packed));
    eqllogPayloadState_t m_state __attribute__((packed));

} eqllogHeader_t;

typedef struct eqllogEvent
{
    __uint32_t m_event __attribute__((packed));
    __uint32_t m_line __attribute__((packed));
    __uint32_t m_size __attribute__((packed));
    __uint32_t m_module_unused __attribute__((packed));
    __uint64_t m_zbus __attribute__((packed));
    __uint64_t m_sequenceNum __attribute__((packed));
    struct timeval m_sent __attribute__((packed));
} eqllogEvent_t ;
struct timeval boottime;

int eqllogQCopyEntry( __caddr_t pDest, int entry, _Bool flag_as_dump )
{
  if( eqllogQInfo->m_iovecs[entry].buf != (void *)0 )
    {
      eqllogHeader_t head;
      __builtin_memcpy(&head, eqllogQInfo->m_iovecs[entry].buf, sizeof(head));
      if (!flag_as_dump)
        {
      eqllogEvent_t event;
      __uint32_t j;
      char *buf = eqllogQInfo->m_iovecs[entry].buf;
      buf += sizeof(head);
      buf += __builtin_strlen(buf) + 1;
      head.m_numEvents = 1;
      for (j = 0; j < head.m_numEvents; j++)
            {
          if (j == 1)
                {

          buf += __builtin_strlen(buf) + 1;
                }
          __builtin_memcpy(&event, buf, sizeof(event));
          if (event.m_sent.tv_sec >= 0 && event.m_sent.tv_sec < 10000)
                {
          event.m_sent = boottime;
          __builtin_memcpy(buf, &event, sizeof(event));
                }
          buf += sizeof(event);
            }
        }

      if( flag_as_dump )
        {
      head.m_uuid.low = head.m_state;
      head.m_state = eqllogPayloadDump;
        } else {
    // ipc_id_to_uuid(ipc_get_pss_id(), &head.m_uuid);
      }

      __builtin_memcpy(pDest, &head, sizeof(head));

      __builtin_memcpy((pDest + sizeof(head)), ((char *)eqllogQInfo->m_iovecs[entry].buf) + sizeof(head), eqllogQInfo->m_iovecs[entry].len - sizeof(head));
    return eqllogQInfo->m_iovecs[entry].len;
    }
  else
    {
      return 0;
    }
}

int main(int argc, char **argv)
{
  char buf[1000];
  char dest[1000];
  sgElement_t iovecs[4];
  
  eqllogQInfo->m_iovecs = iovecs;
  eqllogQInfo->m_iovecs[0].buf = &buf[1];
  eqllogQInfo->m_iovecs[0].len = 40;
  eqllogQCopyEntry (dest, 0, 0);
}
