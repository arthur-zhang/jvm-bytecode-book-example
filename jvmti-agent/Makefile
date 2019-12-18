CC = g++
JAVA_HOME = /Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home

ifeq ($(shell uname), Darwin)
  LIBLINK    = -dynamiclib
  TARGET     = myagent.dylib
  INCLUDEDIR := -I $(JAVA_HOME)/include/ -I $(JAVA_HOME)/include/darwin/
endif

ifeq ($(shell uname), Linux)
  LIBLINK    = -shared -fPIC
  TARGET     = myagent.so
  INCLUDEDIR := -I $(JAVA_HOME)/include/ -I $(JAVA_HOME)/include/linux/
endif

all:
	$(CC) $(LIBLINK)  $(INCLUDEDIR) agent.cpp -o $(TARGET)

clean:
	rm $(TARGET)

