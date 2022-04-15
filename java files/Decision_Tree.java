import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

public class DecisionTree {
	private TreeNode root = null; // stores the root of the decision tree

	public void train(ArrayList<Example> examples) {
		int numFeatures = 0;
		if (examples.size() > 0) // get the number of featuers in these examples
			numFeatures = examples.get(0).getNumFeatures();

		// initialize empty positive and negative lists
		ArrayList<Example> pos = new ArrayList<Example>();
		ArrayList<Example> neg = new ArrayList<Example>();

		// paritition examples into positive and negative ones
		for (Example e : examples) {
			if (e.getLabel())
				pos.add(e);
			else
				neg.add(e);
		}

		// create the root node of the tree
		root = new TreeNode(null, pos, neg, numFeatures);

		// call recursive train() on the root node
		train(root, numFeatures);
	}

	/**
	 * TODO: Complete this method
	 * The recursive train method that builds a tree at TreeNode node
	 * 
	 * @param node:        current node to train
	 * @param numFeatures: total number of features
	 */
	private void train(TreeNode node, int numFeatures) {
		//printTree( node, 1);
		int n_pos = node.pos.size();
		int n_neg = node.neg.size();

		if (n_pos == 0 && n_neg == 0 || (node == null)){
			//set the label to the majority label of its parent's examples
			//find parent, and calc maj label
			TreeNode par = node.parent; //assuming there's always a parent node
			int par_pos = par.pos.size();
			int par_neg = par.neg.size();
			if (par_pos >= par_neg){
				//node label is true; 
				node.decision = true;}
			else{
				//node label is false
				node.decision = false;}
			node.isLeaf = true;

			return;

		}
		
		// base case(1) : if one of them is empty
		else if (n_pos == 0 || n_neg == 0) {
			boolean temp = true; 
			if(n_pos == 0){
				temp = false; //if no pos examples, then all examples are false and label L becomes false
			}
			node.isLeaf = true;
			node.decision = temp;
			return;
		}
		// 4. If no more examples at this node (Base Case 2):
		// 5. -set this node’s label to the majority label of its
		// parent’s examples
		// 6. -set this node as a leaf

		// 7. If no more features (Base Case 3)
		else if (numFeatures == 0){		
			// 8. -set this node’s label to the majority label of this node’s examples
			// 9. -set this node as a leaf
			if (n_pos >= n_neg)
				node.decision = true;
			else 
				node.decision = false;
			node.isLeaf = true;

			return;
		}

		else{
			
			if (node.parent == null ){
				int best_split = calculate_best(node, numFeatures);
				node.setSplitFeature(best_split);
			}
			// should numFeatures and getSplitFeature be different
			// 14. -createSubChildren(node)//each node will have two
			createChildren(node, numFeatures) ;
			// subchildren: a true child and a false child
			train(node.trueChild, numFeatures );
			
			train(node.falseChild, numFeatures);
				// 15. train(this node’s true child)
			// 16. train(this node’s false child)
			}
		
		//possibility for noise in training data, if theres noise you might not get a expected tree
		//noise 

	}


	private int calculate_best(TreeNode node, int numFeatures){

		int best_i = -1;
		double inf_gain = -100000.0;
		double current_e = getEntropy(node.pos.size(), node.neg.size());
		//find best feature to split on
		for(int i =0; i < numFeatures; i++){ //<numFeatures
			// only check for features that havent been used 
			//find best, to know what feature to put at this node.
			if(node.featureUsed(i) == false){ 
				//if feature hasn't been use, calculate information gain if this feature was chosen
				
				if ( current_e - getRemainingEntropy( i, node) > inf_gain){
					best_i = i;
					//best_info_gain = current_e - getRemainingEntropy( i, node);
				}
			}
		}
		return best_i;
	}
	/**
	 * TODO: Complete this method
	 * Creates the true and false children of TreeNode node
	 * 
	 * @param node:        node at which to create children
	 * @param numFeatures: total number of features
	 */
	private void createChildren(TreeNode node, int numFeatures) {
		// 13. Set this node’s feature as f
		//System.out.println(" best one is " + " " + best);
		//node.setSplitFeature(best);

		
		// initialize empty positive and negative lists
		ArrayList<Example> truePos = new ArrayList<>();
		ArrayList<Example> trueNeg = new ArrayList<>();

		ArrayList<Example> falsePos = new ArrayList<>();
		ArrayList<Example> falseNeg = new ArrayList<>();

		// paritition examples into positive and negative ones
		
		int best_split = calculate_best(node,  numFeatures);

		if(best_split == -1){
			//node.trueChild = null;
			//node.falseChild = null;
			return;
		}
		
		node.setSplitFeature(best_split);
		int feature = node.getSplitFeature();


		System.out.println("feature split on  is " + (feature));
		System.out.println("numFeature  is " + (numFeatures));
		//System.out.println( node.parent);
		for( Example e: node.pos){
			if ( e.getFeatureValue(feature) )
				truePos.add(e);
			else
				trueNeg.add(e);
		}

		for( Example e: node.neg){
			if (  e.getFeatureValue(feature))
				falsePos.add(e);
			else
				falseNeg.add(e);
		}
		TreeNode true_child = new TreeNode(node, truePos, falsePos, numFeatures);
		TreeNode false_child = new TreeNode(node, trueNeg, falseNeg, numFeatures);
		//TreeNode(TreeNode par, ArrayList<Example> p, ArrayList<Example> n, int numFeatures)
		//then set node.TrueChild = trueChild_
		//4 arraylist, postrue, (examples w a pos label and a true value for this particular feature)
		node.trueChild = true_child;
		node.falseChild = false_child;

	}

	/**
	 * TODO: Complete this method
	 * Computes and returns the remaining entropy if feature is chosen
	 * at node.
	 * 
	 * @param feature: the feature number
	 * @param node:    node at which to find remaining entropy
	 * @return remaining entropy at node
	 */
	private double getRemainingEntropy(int feature, TreeNode node) {

		
		// // for running without error reasons
		// if ( node.featureUsed(feature) == true) {
		// 	//meaning we already used it, do we return 1.0 so to never get a large inf gain??
		// 	return 1.0; 
		// }
		// double entropy= getEntropy( (node.pos).size(), (node.neg).size());

		int posA =0, negA = 0, posB = 0, negB = 0;

		double total = node.pos.size() + node.neg.size();

		for (Example e: node.pos){ //for eachExample that is positive, is it in the node to the right or in the node to the left
			if (e.getFeatureValue(feature))
				posA++;
			else
				posB++;
		}

		for (Example e: node.neg){
			if (e.getFeatureValue(feature))
				negA++;
			else
				negB++;
		}

		double weight = (posA + negA)/total;
		double entropy = weight * getEntropy(posA, negA) + ((posB+negB)/total) *getEntropy(posB, negB);
		return entropy;
	}

	/**
	 * TODO: complete this method
	 * Computes the entropy of a node given the number of positive and negative
	 * examples it has
	 * 
	 * @param numPos: number of positive examples
	 * @param numNeg: number of negative examples
	 * @return - entropy
	 */
	private double getEntropy(int numPos, int numNeg) {
		// for running without error reasons
		if (numNeg == numPos)
			return 1.0;
		if (numPos == 0 || numNeg == 0)
			//there is no entropy
			return 0.0;
		int total = numPos + numNeg;
		return  ( -1.0 * ((numPos/total) * log2(numPos/total) )) - ((numNeg/total) * log2(numNeg/total) ); 
	}

	/**
	 * Computes log_2(d) (To be used by the getEntropy() method)
	 * 
	 * @param d - value
	 * @return log_2(d)
	 */
	private double log2(double d) {
		return Math.log(d) / Math.log(2);
	}

	/**
	 * TODO: complete this method
	 * Classifies example e using the learned decision tree
	 * 
	 * @param e: example
	 * @return true if e is predicted to be positive, false otherwise
	 */
	public boolean classify(Example e) {
		
		Queue<TreeNode> treeq = new LinkedList<>();

		treeq.add(root);

		while( !treeq.isEmpty() ){
			TreeNode current = treeq.poll();
			int split = current.getSplitFeature();

			if(current.isLeaf)
				return current.decision;
			else{
				boolean feature_val = e.getFeatureValue(split);
			
				if(feature_val)
					treeq.add(current.trueChild);
				else
					treeq.add(current.falseChild);

			}
		}
		return false;
	}

	// ----------DO NOT MODIFY CODE BELOW------------------
	public void print() {
		printTree(root, 0);
	}

	private void printTree(TreeNode node, int indent) {
		if (node == null)
			return;
		if (node.isLeaf) {
			if (node.decision)
				System.out.println("Positive");
			else
				System.out.println("Negative");
		} else {
			System.out.println();
			doIndents(indent);
			System.out.print("Feature " + node.getSplitFeature() + " = True:");
			printTree(node.trueChild, indent + 1);
			doIndents(indent);
			System.out.print("Feature " + node.getSplitFeature() + " = False:");// + "( " + node.falseChild.pos.size() +
																				// ", " + node.falseChild.neg.size() +
																				// ")");
			printTree(node.falseChild, indent + 1);
		}
	}

	private void doIndents(int indent) {
		for (int i = 0; i < indent; i++)
			System.out.print("\t");
	}
}
