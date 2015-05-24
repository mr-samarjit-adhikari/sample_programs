#include <dlfcn.h>
#include <stdio.h>

#define EXTLIB_SNEPLUGIN    "libsneplugin.a"

int main()
{
  void *lib_handle = dlopen(EXTLIB_SNEPLUGIN,RTLD_NOW);
  printf("dlopen error=%s\n",dlerror());

  printf("lib_handle=%p\n",lib_handle);
}
