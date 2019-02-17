# pivot-springboot

An attempt to create a minimal ActivePivot application on top of spring boot.

### Current Status
Application starts and loads data. However, none of the REST/WS endpoints are available.
I think there is some magic that needs to be done with the cxf services (you see in the startup logs that they are registered, but where?!?!)

You can however query the cube from JMX, so the cube is created and that part seems fine.

Also the tests fail, but this is probably due to a missing test dependency