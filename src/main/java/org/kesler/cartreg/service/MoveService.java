package org.kesler.cartreg.service;

import org.kesler.cartreg.domain.Move;

import java.util.Collection;

/**
 * Служба по перемещениям
 */
public interface MoveService {

    public Collection<Move> getAllMoves();
    public void addMove(Move move);
    public void removeMove(Move move);
}
