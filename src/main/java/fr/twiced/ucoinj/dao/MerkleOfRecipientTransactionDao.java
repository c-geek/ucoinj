package fr.twiced.ucoinj.dao;

import fr.twiced.ucoinj.bean.Transaction;
import fr.twiced.ucoinj.bean.id.KeyId;

public interface MerkleOfRecipientTransactionDao extends MultipleMerkleDao<Transaction, KeyId> {

}
