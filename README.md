# Scout24
WebService developed in Java, which takes in a web url, parses the fetched HTML and returns the result.

### Assumptions
User always enters fully qualified url, one at a time 

### Tech Stack
1. DropWizard
2. Junit
3. Eh-cache
4. Maven
5. AngularJs

### Application Flow
```
                                                                                   __________________     
                                                                                  |                  |
                                                                                  | In-Memory Cache/ |
                                                                                  | Executor Service |
                                                                                  |__________________|     
                                                                                           ^
                                                                                           |    
                                                                                           |
    _____________________          _______________         ________________         ________________   
   |                     |        |               |       |                |       |                |              
   | Browser/REST Client |  --->  | REST EndPoint | --->  | Fetch Service  | --->  | Jsoup parser   | 
   |_____________________|        |_______________|       |________________|       |________________|      
                                                                                           ^
                                                                                           |    
                                                                                           |
                                                            _____________         ____________________     
                                                           |             |       |                    |
                                                           | CLI/Service |  ---> | Application Loader | 
                                                           |_____________|       |____________________| 
                                            
```
* Application/Server will fail to start if
   * input config.yml is not provided
   * port specified in the config (8090) is not free
* Cache is loaded for URL suggestion while application is loading
* REST Endpoint will be available for service once the application/server is successfully started.

### Design Considerations
* Extracted from code comments

```
/**
 * DATA STRUCTURE & DESIGN CHOICES
 * -----------------------------
 * REQUIREMENT:
 * -----------
 *      (1) Given the URL fetch the HTML, parse and display the basic details
 *      (2) The input data file - config.yml
 *
 * GOAL:
 * -----
 *      - The fetch query should return the response as soon as possible.
 *      - Given multiple HyperLinks, check for redirection, SSL secure, etc
 *
 * TRADE-OFFs:
 * ----------
 *      (1) Minimal timeout for fetch, to avoid user blocked on one URL.
 *          - Possibility of not getting result for an valid URL.
 *      (2) Some websites might load HTML in fragments. For those only the first returned HTML is considered for parsing
 *          - Since, We will never know, how long we need to wait for the entire page to be loaded or is there more ajax.
 *
 * Single URL
 * -----------
 *      - Calculate the results from a HTML in parallel / sequential.
 *      - Fetch Time Complexity O(Most expensive attribute) / O(Sum of all attributes)
 *
 *      - First approach works, when, extensive analysis is required on a HTML.
 *      Second is being used in this implementation
 *
 * Multiple URL
 * ------------
 *      - Process each URL in parallel, with minimal timeout
 *      - Concurrent List is used intentionally to avoid object level lock in Synchronized list
 *      - Executor service is preferred, as it can be extended for future enhancements
 *      - Limitation of 15 URL's at the moment, if more required the api has to be called again
 *      
 */
 
 ```
 ### Other Notes
 * Logs are printed on the console and intentionally not redirected it to file.

 ### Testing
 * Unit tests and Integration tests will be run during the build/package process.
 * If the test fails, the application won't be packaged.
 
 ### WebClient
 * Simple HTML with Go button for search and Clear button to clear the results
 * Error will be thrown in case of server failure or non 200 response
 * Follow the Internal / External links for hyperlink analysis
  
 ### Enhancements (MultipleLinks)
 * Have multiple systems running the service, each receives URL's based on load and combine the data to send it to client.
 * Client would get first 15 results immediately. As and when server calculates the next chunk it can be sent to client via push notification.
 * Message queue can be used for multiple system. If single system Executor service with scheduled job can be used.
 
 ### Compliance
 1. unzip Scout24.zip
 2. mvn clean install -> Jar is created inside target
      * mvn clean install -DskipTests=true -> To skip tests
 3. java -jar target/scout24-1.0-SNAPSHOT.jar server config.yml 
 4. Hit -> http://localhost:8090/web/#/home