/*
 * DO NOT EDIT.  THIS FILE IS GENERATED FROM /home/dgourlay/EECE496/xpcom/sampletest/IMyComponent.idl
 */

#ifndef __gen_IMyComponent_h__
#define __gen_IMyComponent_h__


#ifndef __gen_nsISupports_h__
#include "nsISupports.h"
#endif

/* For IDL files that don't want to include root IDL files. */
#ifndef NS_NO_VTABLE
#define NS_NO_VTABLE
#endif

/* starting interface:    IMyComponent */
#define IMYCOMPONENT_IID_STR "0566ef76-9f1d-45db-8c68-0103e1595a17"

#define IMYCOMPONENT_IID \
  {0x0566ef76, 0x9f1d, 0x45db, \
    { 0x8c, 0x68, 0x01, 0x03, 0xe1, 0x59, 0x5a, 0x17 }}

class NS_NO_VTABLE NS_SCRIPTABLE IMyComponent : public nsISupports {
 public: 

  NS_DECLARE_STATIC_IID_ACCESSOR(IMYCOMPONENT_IID)

  /* long Add (in long a, in long b); */
  NS_SCRIPTABLE NS_IMETHOD Add(PRInt32 a, PRInt32 b, PRInt32 *_retval NS_OUTPARAM) = 0;

};

  NS_DEFINE_STATIC_IID_ACCESSOR(IMyComponent, IMYCOMPONENT_IID)

/* Use this macro when declaring classes that implement this interface. */
#define NS_DECL_IMYCOMPONENT \
  NS_SCRIPTABLE NS_IMETHOD Add(PRInt32 a, PRInt32 b, PRInt32 *_retval NS_OUTPARAM); 

/* Use this macro to declare functions that forward the behavior of this interface to another object. */
#define NS_FORWARD_IMYCOMPONENT(_to) \
  NS_SCRIPTABLE NS_IMETHOD Add(PRInt32 a, PRInt32 b, PRInt32 *_retval NS_OUTPARAM) { return _to Add(a, b, _retval); } 

/* Use this macro to declare functions that forward the behavior of this interface to another object in a safe way. */
#define NS_FORWARD_SAFE_IMYCOMPONENT(_to) \
  NS_SCRIPTABLE NS_IMETHOD Add(PRInt32 a, PRInt32 b, PRInt32 *_retval NS_OUTPARAM) { return !_to ? NS_ERROR_NULL_POINTER : _to->Add(a, b, _retval); } 

#if 0
/* Use the code below as a template for the implementation class for this interface. */

/* Header file */
class _MYCLASS_ : public IMyComponent
{
public:
  NS_DECL_ISUPPORTS
  NS_DECL_IMYCOMPONENT

  _MYCLASS_();

private:
  ~_MYCLASS_();

protected:
  /* additional members */
};

/* Implementation file */
NS_IMPL_ISUPPORTS1(_MYCLASS_, IMyComponent)

_MYCLASS_::_MYCLASS_()
{
  /* member initializers and constructor code */
}

_MYCLASS_::~_MYCLASS_()
{
  /* destructor code */
}

/* long Add (in long a, in long b); */
NS_IMETHODIMP _MYCLASS_::Add(PRInt32 a, PRInt32 b, PRInt32 *_retval NS_OUTPARAM)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* End of implementation class template. */
#endif


#endif /* __gen_IMyComponent_h__ */
