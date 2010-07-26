CXX   = c++
CPPFLAGS +=     -fno-rtti              \
		-fno-exceptions        \
		-shared

# Change this to point at your Gecko SDK directory.
#GECKO_SDK_PATH = /home/user/gecko-sdk
GECKO_SDK_PATH= /home/dgourlay/Documents/Git/EECE496/websso/implementation/xpcom/xulrunner-sdk
GECKO_LIB_PATH = $(GECKO_SDK_PATH)/lib
GECKO_BIN_PATH = $(GECKO_SDK_PATH)/bin
GECKO_IDL_PATH = $(GECKO_SDK_PATH)/idl

# GCC only define which allows us to not have to #include mozilla-config
# in every .cpp file.  If your not using GCC remove this line and add
# #include "mozilla-config.h" to each of your .cpp files.
#GECKO_CONFIG_INCLUDE = -include mozilla-config.h
GECKO_CONFIG_INCLUDE = -include xpcom-config.h

#GECKO_DEFINES  = -DXPCOM_GLUE

GECKO_INCLUDES = -I $(GECKO_SDK_PATH)/include

#GECKO_LDFLAGS =  -L $(GECKO_SDK_PATH)/lib -lxpcomglue \
#                 -lnspr4      \
#                 -lplds4

# Dependent #
GECKO_DEFINES  = -DXPCOM_GLUE_0
GECKO_LDFLAGS   = -L$(GECKO_LIB_PATH) -L$(GECKO_BIN_PATH) -Wl,-rpath-link,$(GECKO_BIN_PATH) \
                  -lxpcomglue_s -lxpcom -lnspr4

FILES = MyComponent.cpp MyComponentModule.cpp

IDL_FILE_PATH = /home/dgourlay/EECE496/xpcom/sampletest/IMyComponent.idl

TARGET = MyComponent.so

build:
	$(GECKO_BIN_PATH)/xpidl -m header -I $(GECKO_IDL_PATH) $(IDL_FILE_PATH) 
	$(GECKO_BIN_PATH)/xpidl -m typelib -I $(GECKO_IDL_PATH) $(IDL_FILE_PATH) 
	$(CXX) -Wall -Os -o $(TARGET) $(FILES) $(GECKO_SDK_PATH)/lib/libxpcomglue_s.a $(GECKO_LDFLAGS) $(GECKO_CONFIG_INCLUDE) $(GECKO_DEFINES) $(GECKO_INCLUDES) $(CPPFLAGS) $(CXXFLAGS)
	chmod +x $(TARGET)
	strip $(TARGET)

clean:
	rm $(TARGET)
