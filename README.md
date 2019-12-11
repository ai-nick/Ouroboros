# Ouroboros: a p2p neuroevolution framework


State of the repo:
I am currently building out the baseline neat implementation in the elasticnet package of this repo, i have some code for supporting trading tasks in the xchangeservice package and some polo api code in the PoloService package. 


What it will be:

This will eventually be a java app that can run the neat, hyperneat, and es-hyperneat neuroevolution algorithms in a p2p network (it will support running on a single machine as well tho), this will allow faster training by running nets in parallel across several machines, once the paper for the protocol that handles this is finished i will link to it here, this will also be the only open source es hyperneat implementation that supports using n-dimensional substrates which is significant because higher dimension substrates have magnitude greater search spaces to find encodings. It will have a trading task and fitness function that will have a novelty factor in it. At that point i hope to see other devs jump in and write new tasks and fitness functions and get nets running, after this i will likely take it upon myself to build a meta net that will choose the correct net/fitness function from this set of subnets for arbitrary data and/or substrate layouts. The meta net will likely utilize a very high dimension substrate giving it above human brain levels of search space topologically speaking.




Donations (xmr): 4JUdGzvrMFDWrUUwY3toJATSeNwjn54LkCnKBPRzDuhzi5vSepHfUckJNxRL2gjkNrSqtCoRUrEDAgRwsQvVCjZbS4o5B4Qy8ax5mxdD96 
