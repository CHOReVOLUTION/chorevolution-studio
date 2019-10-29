README

1. What are .ADLOAD files?

The .adload files are used to auto-map the choreography item to service item during mapping phase.

2.How to write a correct .ADLOAD file

The file name must be: "service-name".adload
Each row of the file is composed by 6 fields separated by ":"
The special row "-:-" indicates the end of the initiating message mapping.

Example:

"Get Trips Information:0:0:journeyPlanningService:0:0"

The first field is the choreography object name.
The second field is the "first field" type (in this case 0, e.g. task name)
The third field MUST be 0. (now is not used).
The fourth field is the service object name.
The fifth field is the service object type (in this case 0, e.g. operation name)
The sixth field indicates the position of the object in a Depth-First of the service tree of the object.

For example, the following line:
"distance:int:0:x:double:1"

indicates that the choreography object called "distance", having type "int", should be mapped with the
service object "x", type "double", looking at his "second" recurrence ("0" is 1, "1" is 2..) in the service tree.

so, for example:
"distance:int:0:x:double:0
distance:int:0:x:double:1"

maps the first element of the task tree called distance (because appears first) with the "first" element in the service tree called x,
maps the second element of the task tree called distance with the "second" element in a depth-first visit of the service tree called x.

3.Complete example
File News.adload

Get Latest News:0:0:NewsService:0:0
latestNewsRequest:1:0:inputMessageTypeRoot:1:0
latestNewsRequest:3:0:inputMessageTypeRoot:3:0
type:string:0:lat:double:0
lat:double:0:lat:double:0
lon:double:0:lon:double:0
-:-
latestNewsResponse:2:0:outputMessageTypeRoot:2:0
latestNewsResponse:4:0:outputMessageTypeRoot:4:0
events:NewsType:0:root:root:0
name:string:0:start:string:0
description:string:0:objs:objs:0
startDate:dateTime:0:c:string:0
endDate:dateTime:0:cr:string:0
