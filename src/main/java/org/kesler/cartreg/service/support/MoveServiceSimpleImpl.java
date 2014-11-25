package org.kesler.cartreg.service.support;

import org.kesler.cartreg.domain.Move;
import org.kesler.cartreg.service.MoveService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Простая реализация
 */
public class MoveServiceSimpleImpl implements MoveService {
    List<Move> moves = new ArrayList<Move>();

    @Override
    public Collection<Move> getAllMoves() {
        return moves;
    }

    @Override
    public void addMove(Move move) {
        moves.add(move);
    }

    @Override
    public void removeMove(Move move) {
        moves.remove(move);
    }
}
