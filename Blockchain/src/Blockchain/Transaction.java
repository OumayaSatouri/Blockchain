package Blockchain;
import java.security.*;
import java.util.ArrayList;

public class Transaction {
	
	public String transactionId; // this is also the hash of the transaction.
	public PublicKey sender; // senders address/public key.
	public PublicKey reciepient; // Recipients address/public key.
	public float value;
	public byte[] signature; // this is to prevent anybody else from spending funds in our wallet.
	
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	
	private static int sequence = 0; // a rough count of how many transactions have been generated. 
	
	// Constructor: 
	public Transaction(PublicKey from, PublicKey to, float value,  ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
	}
	
	// This Calculates the transaction hash (which will be used as its Id)
	private String calulateHash() {
		sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
		return Utilities.applySha256(
				Utilities.getStringFromKey(sender) +
				Utilities.getStringFromKey(reciepient) +
				Float.toString(value) + sequence
				);
	}
	public void generateSignature(PrivateKey privateKey) {
	String data = Utilities.getStringFromKey(sender) + Utilities.getStringFromKey(reciepient) + Float.toString(value)	;
	signature = Utilities.applyECDSASig(privateKey,data);		
}
//Verifies the data we signed hasnt been tampered with
public boolean verifiySignature() {
	String data = Utilities.getStringFromKey(sender) + Utilities.getStringFromKey(reciepient) + Float.toString(value)	;
	return Utilities.verifyECDSASig(sender, data, signature);
}
public boolean processTransaction() {
		
		if(verifiySignature() == false) {
			System.out.println("#Transaction Signature failed to verify");
			return false;
		}
				
		//gather transaction inputs (Make sure they are unspent):
		for(TransactionInput i : inputs) {
			i.UTXO = Blockchain.UTXOs.get(i.transactionOutputId);
		}

		//check if transaction is valid:
		if(getInputsValue() < Blockchain.minimumTransaction) {
			System.out.println("#Transaction Inputs to small: " + getInputsValue());
			return false;
		}
		if(this.isEnough()==true) System.out.println("Test requirement 02 succeeded ");
		else System.out.println("Test requirement 02 didn't succeed ");
		
		//generate transaction outputs:
		float leftOver = getInputsValue() - value; //get value of inputs then the left over change:
		transactionId = calulateHash();
		outputs.add(new TransactionOutput( this.reciepient, value,transactionId)); //send value to recipient
		outputs.add(new TransactionOutput( this.sender, leftOver,transactionId)); //send the left over 'change' back to sender		
				
		//add outputs to Unspent list
		for(TransactionOutput o : outputs) {
			Blockchain.UTXOs.put(o.id , o);
		}
		
		//remove transaction inputs from UTXO lists as spent:
		for(TransactionInput i : inputs) {
			if(i.UTXO == null) continue; //if Transaction can't be found skip it 
			Blockchain.UTXOs.remove(i.UTXO.id);
		}
		
		return true;
	}
	
//returns sum of inputs(UTXOs) values
	public float getInputsValue() {
		float total = 0;
		for(TransactionInput i : inputs) {
			if(i.UTXO == null) continue; //if Transaction can't be found skip it 
			total += i.UTXO.value; //UTXO est la sortie de i qui est la transaction
		}
		return total;
	}

//returns sum of outputs:
	public float getOutputsValue() {
		float total = 0;
		for(TransactionOutput o : outputs) {
			total += o.value;
		}
		return total;
	}
	public boolean isEnough() {
		if(getInputsValue() < Blockchain.minimumTransaction) return false;
		else return true;
		}
}