# Final Project - Internet Programming

TCP server and client in multi-threading.

Server that handles algorithmic tasks, task set on matrices.
------------------------------------------------------------------
The client can choose between 4 main tasks:
1. find all sccs in the graph (including diagonals)
- input- 2D array of integers
- output- all SCCs ,sorted by size of returned hashset.
example-
-
               {1, 0, 0, 0, 1},
               {0, 0, 1, 0, 0},
               {0, 0, 1, 0, 0},
               {0, 0, 1, 0, 1},
               {0, 0, 1, 0, 1}

         output - [[(0,0)], [(0,4)], [(4,4), (3,4)], [(3,2), (2,2), (1,2), (4,2)]] 


-------------------------------------------------------------------
2. find all the shortest paths between two nodes
- input- 2D array of integers
- output- all the shortest paths between two nodes.
example-
-
               {1, 0, 0, 0, 0},
               {0, 1, 1, 0, 0},
               {0, 1, 1, 0, 0},
               {0, 0, 1, 0, 0},
               {0, 0, 1, 0, 0}

                output from (0,0) to (4,2) will be:
                [[(0,0), (1,1), (2,1), (3,2), (4,2)], [(0,0), (1,1), (2,2), (3,2), (4,2)]]
(2.1) Asynchronously
(2.2) in parallel 

-------------------------------------------------------------------
3. find all submarines in matrix
"submarine" must maintain the following rules:
- 1) Minimum of two "1" vertically.
- 2) Minimum of two "1" horizontally.
- 3) There cannot be "1" diagonally unless arguments 1 and 2 are implied.
- 4) The minimal distance between two submarines is at least one index("0").

- input- 2D arrayof integers
- output- number of proper submarines
example-
-
                {1, 1, 0, 0, 1},
                {0, 0, 0, 0, 1},
                {0, 0, 1, 0, 0},
                {1, 0, 1, 0, 0},
                {1, 0, 1, 0, 1}

                output - 4

-------------------------------------------------------------------
4. find all lightest weight paths between two nodes (also support negative weights)
(4.1) Asynchronously
(4.2) in parallel
- input- 2D array of integers 
- output -all the lightest weight paths between two nodes.
exmaple-
-
                {100,100,100},
                {300,900,500},
                {100,100,100}

                output from (1,0) to (1,2) will be:
                [[(1,0),(0,1),(1,2)],[(1,0),(2,1),(1,2)]] , with weight 900 - it also includes diagonals.
