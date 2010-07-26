#include "/home/dgourlay/Documents/Git/EECE496/websso/implementation/xpcom/xulrunner-sdk/include/nsIGenericFactory.h"
#include "MyComponent.h"

NS_GENERIC_FACTORY_CONSTRUCTOR(MyComponent)

static nsModuleComponentInfo components[] =
{
    {
       MY_COMPONENT_CLASSNAME,
       MY_COMPONENT_CID,
       MY_COMPONENT_CONTRACTID,
       MyComponentConstructor,
    }
};

NS_IMPL_NSGETMODULE("MyComponentsModule", components) 
