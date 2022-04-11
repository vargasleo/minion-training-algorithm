# minion-training-algorithm
_algorithms and data structures II - PUC RS_

Implementing a directed graph of obstacles that must be overcome with the fewest possible minions and the shortest time possible.

1. Input is a txt file where each line represents an vertex 
2. Ex: (ABC_19 -> CDE_20) Node ABC takes 19 seconds and must be completed before CDE, that takes 20 seconds
4. Only one minion work per obstacle
5. Lots of minions can work in lots of obstacles each time
6. When an obstacle is ready to be completed, one minion is sent for execution
7. Minions prioritize obstacles by alfabetic order

You can check the detailed problem statement [here](https://github.com/vargasleo/minion-training-algorithm/blob/master/problem-statement.pdf) (pt-br).

And my complete report, containing time-complexity analysis of the algorithm, [here](https://github.com/vargasleo/minion-training-algorithm/blob/master/latex-report.pdf) (pt-br).
