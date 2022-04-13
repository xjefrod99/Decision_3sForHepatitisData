import java.util.ArrayList;

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
		// 1. If all remaining examples at this node have the same label L (Base Case 1
		// 2. -set this node’s label to L
		// 3. -set this node as a leaf
		if (n_pos == 0 || n_neg == 0) {
			
			boolean temp = true; 
			if(n_pos == 0){
				temp = false; //if no pos examples, then all examples are false and label L becomes false
			}
			node.isLeaf = true;
			node.decision = temp;
		}
		// 4. If no more examples at this node (Base Case 2):
		// 5. -set this node’s label to the majority label of its
		// parent’s examples
		// 6. -set this node as a leaf
		if (n_pos == 0 && n_neg == 0){
			//set the label to the majority label of its parent's examples

			//find parent, and calc maj label
			TreeNode par = node.parent;
			int par_pos = par.pos.size();
			int par_neg = par.neg.size();
			if (par_pos >= par_neg){
				//node label is true; 
				node.decision = true;}
			else{
				//node label is false
				node.decision = false;}
			node.isLeaf = true;

		}

		// 7. If no more features (Base Case 3)
		if (numFeatures == 0){		
			// 8. -set this node’s label to the majority label of this node’s examples
			// 9. -set this node as a leaf
			if (n_pos >= n_neg){
				//node label is true;  do we set boolean decision in the TreeNode class
				node.decision = true;
			}
			else{
				//node label is false
				node.decision = false;
			}
			node.isLeaf = true;
		}

		else{
			// 11. pos = node.getPos(); neg = node.getNeg(); // Get the positive and negative examples for this node
			ArrayList<Example> pos_nodes = node.pos;
			ArrayList<Example> neg_nodes = node.neg;
			int split = node.getSplitFeature();
			node.featureUsed(split); //set to true bc we used it

			//find next feature to split on; 
			int decisive_feature = 0; 
			int best = 0;
			double max = -1.0;
			double current_e = (getEntropy(node.pos, node.neg);
			// 12. Find the next feature to split on, i.e. the feature, f, (what do we set it to) with the most information gain
			while(decisive_feature != split){
				//we go through all features up to this node
				// ** is this how we calculate inf gain? replace split with what? **//
				if ( current_e - getRemainingEntropy(decisive_feature, node) > max){
					best = decisive_feature;
				}
				decisive_feature++;
			}
			// 13. Set this node’s feature as f
			node.setSplitFeature(best);

			// 14. -createSubChildren(node)//each node will have two
			createChildren(node, numFeatures) ;
			// subchildren: a true child and a false child
			train(node.trueChild, best);
			
			train(node.falseChild, best);
			// 15. train(this node’s true child)
			// 16. train(this node’s false child)
		}
		

	}

	/**
	 * TODO: Complete this method
	 * Creates the true and false children of TreeNode node
	 * 
	 * @param node:        node at which to create children
	 * @param numFeatures: total number of features
	 */
	private void createChildren(TreeNode node, int numFeatures) {

		// initialize empty positive and negative lists
		ArrayList<TreeNode> trueChild_ = new ArrayList<TreeNode>();
		ArrayList<TreeNode> falseChild_ = new ArrayList<TreeNode>();

		// paritition examples into positive and negative ones

		//then set node.TrueChild = trueChild_

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

		// for running without error reasons
		if ( node.featureUsed(feature) == true) {
			//meaning we already used it, do we return 1.0 so to never get a large inf gain??
			return 1.0; 
		}
		double entropy= getEntropy( (node.pos).size(), (node.neg).size());
		//how is a feature chosen at node?
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
		int total = numPos + numNeg;
		return  ((numPos/total) * log2(numPos/total) )+ ((numNeg/total) * log2(numNeg/total) ); 
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


		// for running without error reasons
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
