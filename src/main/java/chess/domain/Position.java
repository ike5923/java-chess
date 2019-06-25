package chess.domain;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Position {
    private static final int MIN_LIMIT = 1;
    private static final int MAX_LIMIT = 8;
    private static final int SQUARE_UNIT = 2;
    private static final int FIRST_ROW_FOR_WHITE_PAWN = 2;
    private static final int FIRST_ROW_FOR_BLACK_PAWN = 7;

    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
        validatePosition();
    }

    private void validatePosition() {
        if (isOutOfRange(x) || isOutOfRange(y)) {
            throw new IllegalArgumentException("체스판을 넘었습니다.");
        }
    }

    private boolean isOutOfRange(int number) {
        return number < MIN_LIMIT || number > MAX_LIMIT;
    }

    public boolean canMoveBackAndForth(Position position) {
        return this.x == position.x;
    }

    public boolean canMoveSideToSide(Position position) {
        return this.y == position.y;
    }

    public boolean canMovePositiveDiagonally(Position position) {
        return subtractY(position) == subtractX(position);
    }

    public boolean canMoveNegativeDiagonally(Position position) {
        return subtractY(position) + subtractX(position) == 0;
    }

    public int getDistanceSquare(Position position) {
        return (int) (Math.pow(subtractY(position), SQUARE_UNIT) + Math.pow(subtractX(position), SQUARE_UNIT));
    }

    private int subtractX(Position position) {
        return this.x - position.x;
    }

    public int subtractY(Position position) {
        return this.y - position.y;
    }

    public boolean isInStartingPosition() {
        return this.y == FIRST_ROW_FOR_WHITE_PAWN || this.y == FIRST_ROW_FOR_BLACK_PAWN;
    }

    public List<Position> getRoutePosition(Position position) {
        List<Position> routePositions = new ArrayList<>();

        List<Integer> xValues = IntStream.rangeClosed(Math.min(this.x, position.x) + 1, Math.max(this.x, position.x) - 1)
                .boxed()
                .collect(Collectors.toList());
        List<Integer> yValues = IntStream.rangeClosed(Math.min(this.y, position.y) + 1, Math.max(this.y, position.y) - 1)
                .boxed()
                .collect(Collectors.toList());

        if (canMoveSideToSide(position)) {
            routePositions.addAll(getSideToSideRoute(xValues));
        }

        if (canMoveBackAndForth(position)) {
            routePositions.addAll(getBackAndForthRoute(yValues));
        }

        if (canMovePositiveDiagonally(position)) {
            routePositions.addAll(getPositiveDiagonally(xValues, yValues));
        }

        if (canMoveNegativeDiagonally(position)) {
            routePositions.addAll(getNegativeDiagonallyRoute(xValues, yValues));
        }

        return routePositions;
    }

    private List<Position> getSideToSideRoute(List<Integer> xValues) {
        return xValues.stream()
                .map(x -> new Position(x, y))
                .collect(Collectors.toList());
    }

    private List<Position> getBackAndForthRoute(List<Integer> yValues) {
        return yValues.stream()
                .map(y -> new Position(x, y))
                .collect(Collectors.toList());
    }

    private List<Position> getPositiveDiagonally(List<Integer> xValues, List<Integer> yValues) {
        List<Position> routePositions = new ArrayList<>();

        for(int i = 0; i < xValues.size(); i++){
            routePositions.add(new Position(xValues.get(i), yValues.get(i)));
        }

        return routePositions;
    }

    private List<Position> getNegativeDiagonallyRoute(List<Integer> xValues, List<Integer> yValues) {
        List<Integer> reversedYValues = new ArrayList<>(yValues);
        Collections.reverse(reversedYValues);

        List<Position> routePositions = new ArrayList<>();

        for(int i = 0; i < xValues.size(); i++){
            routePositions.add(new Position(xValues.get(i), reversedYValues.get(i)));
        }

        return routePositions;
    }

    public static double getDuplicatedItemsCount(List<Position> pawnPosition) {
        List<Integer> xValuesPawnHas = pawnPosition.stream()
                .map(position -> position.x)
                .collect(Collectors.toList());

        Set<Integer> unique_x = new HashSet<>(xValuesPawnHas);

        return unique_x.stream()
                .map(x -> Collections.frequency(xValuesPawnHas, x))
                .filter(count -> count >= 2)
                .mapToInt(Integer::intValue)
                .sum();
    }

    public boolean isMatchPosition(int x, int y) {
        return (this.x == x) && (this.y == y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x &&
                y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return x + "" + y;
    }
}