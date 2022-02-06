package Blockchain;


import java.util.ArrayList;
import com.google.gson.GsonBuilder;

public class Blockchain {
	
	public static ArrayList<Block> blockchain = new ArrayList<Block>(); 
	public static int difficulty = 5; //la difféculté qui doit etre résolue par les mineurs

	
	public static void main(String[] args) {
		
		/*
		 * Method 1: add blocks to the blockchain ArrayList
		 * more realistic
		 * form of Json 
		 */
		blockchain.add(new Block("The first block", "0"));		
		System.out.println("Trying to Mine block 1... ");
		blockchain.get(0).mineBlock(difficulty);
		
		blockchain.add(new Block("The second block",blockchain.get(blockchain.size()-1).hash)); 
		System.out.println("Trying to Mine block 2... ");
		blockchain.get(1).mineBlock(difficulty);
		
		blockchain.add(new Block("The third block",blockchain.get(blockchain.size()-1).hash));
		System.out.println("Trying to Mine block 3... ");
		blockchain.get(2).mineBlock(difficulty);
		/*
		blockchain.add(new Block("The forth block",blockchain.get(blockchain.size()-1).hash));
		System.out.println("Trying to Mine block 4... ");
		blockchain.get(3).mineBlock(difficulty);
		
		blockchain.add(new Block("The fifth block",blockchain.get(blockchain.size()-1).hash));
		System.out.println("Trying to Mine block 5... ");
		blockchain.get(4).mineBlock(difficulty);*/

		
		System.out.println("\nBlockchain is Valid: " + isChainValid());
		
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println("\nThe block chain: ");
		System.out.println(blockchainJson);
	}
	
	/*
	 * Toute modification des blocs de la blockchain entraînera le retour de cette méthode à false.
	 */
	public static Boolean isChainValid() {
		Block currentBlock; 
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		
		//loop through blockchain to check hashes:
		for(int i=1; i < blockchain.size(); i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			//compare registered hash and calculated hash:
			if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
				System.out.println("Current Hashes not equal");			
				return false;
			}
			//compare previous hash and registered previous hash
			if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
				System.out.println("Previous Hashes not equal");
				return false;
			}
			//check if hash is solved
			if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
				System.out.println("This block hasn't been mined");
				return false;
			}
		}
		return true;
	}
}