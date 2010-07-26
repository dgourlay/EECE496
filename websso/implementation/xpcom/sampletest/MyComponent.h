#ifndef _MY_COMPONENT_H_
#define _MY_COMPONENT_H_

#include "IMyComponent.h"

#define MY_COMPONENT_CONTRACTID "@example.com/XPCOMSample/MyComponent;1"
#define MY_COMPONENT_CLASSNAME "A Simple XPCOM Sample"
#define MY_COMPONENT_CID { 0xd5d15142, 0xbd44, 0x424d, \
  { 0x8c, 0x34, 0xbd, 0xa9, 0x74, 0xf0, 0x62, 0x81 } }

class MyComponent : public IMyComponent
{
public:
  NS_DECL_ISUPPORTS
  NS_DECL_IMYCOMPONENT

  MyComponent();

private:
  ~MyComponent();

protected:
  /* additional members */
};
#endif //_MY_COMPONENT_H_         
