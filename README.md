# Ouroboros: a p2p neuroevolution framework


State of the repo:
I am currently building out the baseline neat implementation in the elasticnet package of this repo, i have some code for supporting trading tasks in the xchangeservice package and some polo api code in the PoloService package. 


What it will be:

This will eventually be a java app that can run the neat, hyperneat, and es-hyperneat neuroevolution algorithms in a p2p network (it will support running on a single machine as well tho), this will allow faster training by running nets in parallel across several machines, once the paper for the protocol that handles this is finished i will link to it here, this will also be the only open source es hyperneat implementation that supports using n-dimensional substrates which is significant because higher dimension substrates have magnitude greater search spaces to find encodings. It will have a trading task and fitness function that will have a novelty factor in it. At that point i hope to see other devs jump in and write new tasks and fitness functions and get nets running, after this i will likely take it upon myself to build a meta net that will choose the correct net/fitness function from this set of subnets for arbitrary data and/or substrate layouts. The meta net will likely utilize a very high dimension substrate giving it above human brain levels of search space topologically speaking.


white paper: 

OuroborosChain: A p2p Neuroevolution System
Nick

Abstract:
    Abstract. About 10 years ago a peer-to-peer electronic cash system was created called Bitcoin. Many are probably familiar with Bitcoin and its Proof-of-Work (PoW) consensus; proposed in the following paper is a concept relatively similar to this system but with a different use case and consensus protocol(s). The intent of this system is to provide an easy to run application that allows anyone with a laptop and internet access the ability to help train artificial neural networks using neuroevolution and to have access to these nets to utilize for fun or profit. A champion net will also serve as a currency of sorts: with non participating parties being able to purchase champion net with the [redacted] cryptocurrency. The price paid will be recorded by each node which serves as an indicator of the genome network’s value. The consensus protocols (proof of fitness and proof of value) serve to verify the benevolence of participating nodes and verify the transaction history of previous champion genomes. In this paper the first implementation of a network utilizing this protocol is described. This implementation solves some of the issues that are commonly talked about in deep learning including transparency, friendliness, and democratization while also providing durability, persistence and potentially high availability. Given we can achieve a degree of high availability that also means the nets could be trained “online” as well as being able to reliably layer nets into a hierarchy that encourages sentient like behavior.   

Introduction


Cryptocurrencies have grown in development, adoption, funding, and proposed use cases in the years since 2009 when the most popular cryptocurrency to date, Bitcoin, was first released. Alongside the hype and boom in the crypto space, the field of artificial intelligence has also been ripe with new developments, funding, and at the corporate level, institutional investment. The biggest difference in the spaces  is the accessibility and benefit to the general public: crypto is very accessible and (for public chains) can benefit the general public in a wide variety of ways; artificial intelligence, and deep learning in particular, is generally not very accessible to the public (need access to high amounts of computational power to rival corporations). Further, the value is non existent outside the corporations that use their access to massive amounts of infrastructure to profit from AI (often at the expense of the average individual). 
 What is needed is a publicly accessible system that pairs cryptographic signatures with a set of validation steps that allows individuals to combine their computing resources to train artificial neural networks on deep learning tasks in a way that is mutually beneficial to all participants. Further, such a system should be flexible as to allow aspiring developers in artificial intelligence the ability to create new networks of this variety by simply shaping there data appropriately and writing fitness functions that conform to the conventions in place, (pr’s can handle this tyranny in practice). 

Training
Nueroevolution is a deep learning technique that uses genetics and populations of
Artificial Neural Networks (ANNs) to solve tasks. I have chosen to use this technique over other deep learning techniques for several reasons. The two most significant being the ability to train in parallel and the adversity to overfitting that are both characteristic of the technique. In order to ensure the validity of the training with a peer to peer network where each node evaluates a subset of the population this paper proposes a protocol called “proof of fitness”. 

Proof of Fitness
To implement a distributed training environment on a public p2p basis a proof-of-fitness system is needed. The required work to find a genome that performs better than all others in the population is variable, but on average this difficulty increases as the process of evolution continues. . This is analogous to the increase in difficulty to double spend with bitcoin.  Unlike bitcoin however, if someone introduced a rogue genome that wasn’t part of the population but still performs better than its counterparts, it would be to the benefit of the whole network.

With that in mind, we need a way of verifying that all participants are evaluating the correct networks and broadcasting valid fitness values for their genomes. To do this,two steps of verification per generation of evolution are required. The first step involves every node randomly picking a peer to check. This peer will randomly choose a genome the other node evaluated and reported a fitness value for; then it will evaluate the genome on the same training set of data to ensure that the genome’s fitness is indeed what was reported  Once all peers have been verified, then every peer node in the network will evaluate the genome that reported the highest fitness to ensure it wasn’t a false claim made by a bad acting node. Once a consensus has been reached regarding the best performing genome, all nodes will save the genome to disk, as well as the public keys of all verified participants for that generation to a chain of all previous generational champions. By default the consensus that the champ is valid is set to > 50% of the nodes agreeing that the fitness is valid, although this value will be configurable and thus can be set by the developer of the fitness function and the genesis node operator to be either higher or lower. 

Proof of Value
To provide a reward for participation beyond the personal access to the champion networks, and to validate the value of any champion network, a rather light weight protocol for adding value to champions in the chain is proposes. In this protocol genomes can give access to previous genomes on the chain in return for a payment in cryptocurrency. To prevent nodes from artificially inflating the worth of a previous genome, a protocol for the sale to be recorded and added to the genomes value is employed. That protocol involves spreading the profits of that sale to other participants of that generation and using peer gossip until all generations have acknowledged they received payment. By requiring the seller to share the profits of the sale in order for the network to accept  it as valid, the node would be forced into rewarding and proving that funds were indeed received as part of the process to increase the recorded value of the genome.

Network
The steps to run training in a network are as follows:
A randomly selected node generates the population and selects a batch of training data. It then broadcasts it to all other nodes with their subset of ids to evaluate the specified model as well.
Each node works on evaluating the fitness of the genomes it selected for the given training set and then broadcasting the results to all nodes as they complete evaluation of each genome. 
Once the evaluation is complete for the whole population the nodes begin checking each other’s  fitness values. 
Once all nodes have been checked any bad actor’s genomes are redistributed, the network repeats steps 3 and 4 until all genomes have been evaluated by trustworthy nodes.
Finally, every node evaluates and verifies the highest performing genome and stores it along with all the public keys of verified participants as a new block in the chain, including all previous champs and the public keys that have access to them.

The steps for reporting the sale of a genome for some monetary value are as follows:
The seller node broadcasts the sale and the amount for some previous champion to the whole network.
Nodes that have the genome stored will request their “share” of the proposed payment, and broadcast a confirmation upon receiving their payment.
Once all nodes that are in the block for that genome and requested payment have confirmed they received payment, all nodes will add the total amount to the total value of the genome on the chain. 

Note: If a request by a node that has its key in the genome that was allegedly sold goes unfulfilled, the sale should be considered invalid and thus not be added to the genome’s value attribute. 

Incentive
By convention, honest participation is incentivised by both protocols proposed here. The incentive to participate benevolently in training is the ability to use as you see fit the best genomes that are found for completing the task at hand. In this same vein, by participating honestly and being recorded on the blockchain as an honest node for a generation the peer has the ability to market and sell the genome; they can do so without sharing, but sharing with the other participants allows them to have the value added onto the chain which can then be used to further market and sell the network.

Calculations
Since any malevolence in the network simply reduces the value of the network and has no upside for the bad actor, will likely prevent them from gaining any benefit from participating, and/or involves other nodes claiming they received payment that they in fact did not receive, there is not a significance in mathematically formalising the likelihood of invalid genomes being accepted as valid, or of value falsely being attributed to a champion block. In this way the proposed system is significantly unlike the PoW or PoS calculations associated with cryptocurrencies like bitcoin. Rather, what is worth proving is the advantage of utilizing distributed training for a deep learning task. By running networks in parallel and using proof of fitness as a consensus of that evaluation, we can calculate the time saved for training. 
    
N = number of nodes
G = number of genomes in a population
T = average time to evaluate a single net
E = evals per peer node (G / N) + 2
S = time saved by distributed eval (T * N) - (E *T) 

To be safe,assume there is a 10% cost to network overhead 
on average, for the two steps in proof of fitness and some imperfect parallelization.Thus we set G, N, and T to 10000, 50, and 5 respectively:
 
S = ((10000* 5) - (202*5)) *.9 which gives us 44,091 sec saved, or 5,909 sec
runtime per generation as compared to 50000 seconds, or to compare, an 88% reduction.

Conclusion
 Without p2p artificial intelligence systems it is very unlikely that the masses can benefit from advancements in AIai. In fact, it is likely the opposite where only big companies and governments benefit and the general public is exploited (yet again) given the trends thus far in the AI sector.  This paper proposes a system that uses p2p protocols and neuroevolution to evolve neural networks which anyone who helps train can then use, reducing the barrier to entry for  both the development of and the ability to benefit from deep learning models trained with substantial and essentially costless    amounts of computational power.



