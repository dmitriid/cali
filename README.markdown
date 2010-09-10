*cali* is a simple interface to databases supported by [Gremlin](http://gremlin.tinkerpop.com/) — an amazing library for querying graph databases. See these presentations to see what I mean: [1](http://www.slideshare.net/slidarko/computing-with-directed-labeled-graphs-3879998), [2](http://www.slideshare.net/slidarko/graph-windycitydb2010), [3](http://www.slideshare.net/slidarko/problemsolving-using-graph-traversals-searching-scoring-ranking-and-recommendation). So far *cali* supports neo4j and TinkerGraph, though it's quite easy to to extend and develop *cali* further, to support any database that Gremlin/[Pipes](http://pipes.tinkerpop.com/)/[Blueprints](http://blueprints.tinkerpop.com/) support.

*cali* is a single-threaded simple and dumb library for more or less simple queries. For multithreaded goodness see [Nerlo](http://github.com/nerlo/nerlo).

Dependencies
---

* Gremlin 0.5 Snapshot standalone package, [http://gremlin.tinkerpop.com/](http://gremlin.tinkerpop.com/)
  
  Gremlin standalone resolves a host of dependencies for you: Gremlin, Pipes, Blueprints, neo4j

* Erlang (with jinterface)

These are resolved through included maven project. If you don't want to build stuff yourself, there's a compiled package in the [download section](http://github.com/dmitriid/cali/downloads).

Starting cali
--------

*cali* is implemented as an independent Java node:  

    java -classpath cali.jar com.dmitriid.cali.Main [OPTIONS]

where options are:

* `-n`, name of the node. default is `'cali@localhost'`
* `-m`, name of the mailbox. default is `'mbox'`
* `-c`, cookie, if any. default is none
* `--connector`, class of database connector to use. default is `com.dmitriid.cali.db.Neo4JConnector`

Of the four options above all are optional.

See `com.dmitriid.cali.db.Neo4JConnector` and `com.dmitriid.cali.db.TinkerGraphConnector` to see how to implement your own database connector

Your connector may require additional options, just append them to the options above (see example towards the end of this README).


**Neo4JConnector options**

* `-d`, `--db_path`. path to neo4j database, required.

**OrientDBConnector options**

* `-u`, `--url`. url of orientdb database, required.
* `--user`. username, required.
* `--pass`. password, required

API
---

*cali* currently only has to ways to get and set data. These are... the `get` and the `set` methods. However, these are quite versatile in what they can do:

* `get`
  Syntax: `{get, WhatToRetrieve}`
  
  WhatToRetrieve is any of the following:

  * `{vertex, Id}`
  * `{vertex, Id, [ListOfPropertyNames]}`, if you wish to retrieve several property values from a node
  * `{edge, Id}`
  * `{q, Query}`, if you want to run an ad-hoc Gremlin query

  These can also be passed in as a list:  
  `[{vertex, 1}, {edge, 2}, {vertex, 45, [name]}, {q, "$_/inE"}]` etc.

* `set`

  Syntax: `{set, WhatToSet}`
  
  WhatToSet is any of the following:

  * `{vertex}`, only create a vertex
  * `{vertex, [Properties]}`, a proplist of properties and their values
  * edge specifications, see "Setting edges" below

  These can also be passed as a list:
  `[{vertex}, {vertex, [{name, "A vertex"}, {prop, "A prop"}]}]`

Setting edges
---

In order to set an edge, you need to have incoming edges and outgoing edges. How do we get them? Well, using the `get` method, of course.

* `edge specs`

   * `{'<-', Label}`, from vertex set on the right to the vertex set on the left, "incoming" edge
   * `{'->', Label}`, from vertex set on the left to the vertex set on the right, "outgoing" edge
   * `{'<->', Label}`, create both edges at once
   
**Example. Setting edges**

So here's how we can set a "knows" relationship between two nodes:

    [
     {get, {vertex, 1}},
     {set, {'->', "knows"}},
     {get, {vertex, 2}}
    ]

Or vice versa:

    [
     {get, {vertex, 1}},
     {set, {'<-', "is_known_by"}},
     {get, {vertex, 2}}
    ]

Or reciprocal:

    [
     {get, {vertex, 1}},
     {set, {'<->', "friend_of"}},
     {get, {vertex, 2}}
    ]

Working with sets
---

Remember, we said that both `get` and `set` accept a list of things to retrieve/set? Well, it works for setting edges, as well:

**Example. Banning several users at once**

    [
     {get, {q, "$_/outE/inV[@name='ban_list']"}},  %% retrieve the ban-list node
     {set, {'->', "contains"}},
     {get, 
        [
           {vertex, 1234},    %% if we know the exact node id
           {q, "$_/outE/inV[@name='users']/outE/inV"}  %% we also decide to ban all users at once. Why can't we?
        ]
     }
    ]

Regardless of the number of retrieved nodes in the second `get` call, there will be an edge labeled `contains` from vertex 1 to each of these nodes

This, of course, can easily be reversed:

    [
     {get, 
        [
           {vertex, 1234},    %% if we know the exact node id
           {q, "$_/outE/inV[@name='users']/outE/inV"}  %% we also decide to ban all users at once. Why can't we?
        ]
     },
     {set, {'<-', "is_in"}},
     {get, {q, "$_/outE/inV[@name='ban_list']"}},  %% retrieve the ban-list node
    ]


Edges for newly created nodes
----

One cool thing about `set` is that it returns a set of newly created objects. So, if you want to create an edge while creating nodes
    [
     {set, [{vertex}, {vertex}, {vertex}]},
     {set, [{'<-', "in"}, {'->', "out"}]},
     {set, [{vertex}, {vertex}, {vertex}]},
    ]


More examples
---

So, let's fire it up and play with it:

    $ java -classpath cali.jar com.dmitriid.cali.Main -d ~/Projects/java/neo4j/db
      .... or ... java -classpath cali.jar com.dmitriid.cali.Main --connector com.dmitriid.cali.db.TinkerGraphConnector
    $ erl -sname client
    erl> F = fun(Q) -> 
                {'mbox', 'cali@localhost'} ! {self(), Q},
                receive
                  Any -> Any
                end
         end.
    #Fun<erl_eval.6.13229925>

    erl>
    erl> F([
             {get, {vertex, 0}},  %% the root. could've also done {q, "$_"}
             {set, {'->', "child"}},
             {set, {vertex, [{name, "Users"}]}},
             {set, {'->', "user"}},   %% yep, you can just go on and chain commands
                                      %% since every command returns a set of nodes it created/retrieved
             {set, [{vertex, [{name, "User1"}]},
                    {vertex, [{name, "User2"}]},
                    {vertex, [{name, "User3"}]}]}
           ]).

    [{"vertex",[{"id",2},{"name","User1"}]},
     {"vertex",[{"id",3},{"name","User2"}]},
     {"vertex",[{"id",4},{"name","User3"}]}]

    erl> F([{get, {q, "$_/outE/inV[@name='Users']/outE/inV[@name='User1']"}}]).  %% get User1
    
    [{"vertex",[{"id",2},{"name","User1"}]}]

    erl> F([{get, {vertex, 3}}, {set, {'<->', "friend_of"}}, {get, {vertex, 4}}]).  %% set frendship information
    
    {"vertex",[{"id",4},{"name","User3"}]}

    erl> F([{get, {q, "$_/outE/inV[@name='Users']/outE/inV/@name"}}]).  %% get names of all users
    
    [["User3","User2","User1"]]

    erl> F([{get, [{vertex, 1}, {vertex, 3, [name]}]}]).  %% get only some properties for a node
    
    [{"vertex",[{"id",1},{"name","Users"}]},
     {"vertex",[{"name","User2"}]}]

    erl> F([{get, [{vertex, 2, [name, id]}, {q, "$_/@id"}]}]).  %% get mixed results
    
    [{"vertex",[{"name","User1"}]},0]

    erl> F(exit). %% shut the node down

    exit

As a result we will have a graph like this one:

![graph](http://files.dmitriid.com/images/cali/graph.gif)
