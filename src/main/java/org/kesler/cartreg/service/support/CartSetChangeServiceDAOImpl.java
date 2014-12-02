package org.kesler.cartreg.service.support;

import org.kesler.cartreg.dao.CartSetChangeDAO;
import org.kesler.cartreg.domain.CartSetChange;
import org.kesler.cartreg.service.CartSetChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Created by alex on 01.12.14.
 */
@Transactional
@Service("cartSetChangeService")
public class CartSetChangeServiceDAOImpl implements CartSetChangeService {

    @Autowired
    private CartSetChangeDAO cartSetChangeDAO;

    @Transactional(readOnly = true)
    @Override
    public Collection<CartSetChange> getAllChanges() {
        return cartSetChangeDAO.getAllChanges();
    }


    @Override
    public void addChange(CartSetChange change) {
        cartSetChangeDAO.addChange(change);
    }


    @Override
    public void removeChange(CartSetChange change) {
        cartSetChangeDAO.removeChange(change);
    }
}
