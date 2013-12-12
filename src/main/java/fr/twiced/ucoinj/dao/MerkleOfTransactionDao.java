package fr.twiced.ucoinj.dao;

import fr.twiced.ucoinj.bean.Transaction;
import fr.twiced.ucoinj.bean.id.TransactionId;

public interface MerkleOfTransactionDao extends MultipleMerkleDao<Transaction, TransactionId> {

}
