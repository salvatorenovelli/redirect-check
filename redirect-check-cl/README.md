[![Build Status](https://travis-ci.org/salvatorenovelli/redirect-check-cl.svg?branch=master)](https://travis-ci.org/salvatorenovelli/redirect-check-cl)
# redirect-check-cl

A command-line utility to verify that a given set of URL correctly redirect to the expected location. 

###Input:
Takes a CSV file as an imput formatted as `sourceURI`,`expectedDestinationURI`

Example: 

    http://example.com, http://www.example.com
    http://www.example.com/nonexistentpage, http://www.example.com/notfound
    ...etc...
    

###Output
A csv file with the actual redirect destination, HTTP status code, result (as is SUCCESS or FAILURE) result of every redirect in the input.
   


##Context
In [SEO][2], during a website structure/domain migration is common to have a very long list of URLs that need to be redirected to another location, and this list needs to be checked periodically for completion and regression.

Creating such list is already cumbersome but verifying it (periodically) is repetitive, therefore should (must!) be automated. 

In this project I'll use Spring Cloud and [Spring Cloud Stream][1] concepts, and once working, I'll migrate it to [Reactive Streams][3]


  [1]: https://cloud.spring.io/spring-cloud-stream/
  [2]: https://en.wikipedia.org/wiki/Search_engine_optimization
  [3]: https://spring.io/blog/2016/02/09/reactive-spring
