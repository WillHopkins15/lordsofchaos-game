package org.lordsofchaos.matrixobjects;

import org.lordsofchaos.coordinatesystems.MatrixCoordinates;

public abstract class MatrixObject implements Comparable<MatrixObject>
{
    // this is not a real-world position, so when constructing a matrixObject, only pass in
    // matrixPositions
    protected MatrixCoordinates matrixPosition;

    public MatrixObject(int y, int x) {
        setMatrixPosition(y, x);
    }

    public void setMatrixPosition(int y, int x) {
        matrixPosition = new MatrixCoordinates(y, x);
    }
    
    public MatrixCoordinates getMatrixPosition() {
        return matrixPosition;
    }

    @Override
    public int compareTo(MatrixObject o) {
        int thisSum = matrixPosition.getX() + matrixPosition.getY();
        int otherSum = o.matrixPosition.getX() + o.matrixPosition.getY();
        return Integer.compare(otherSum, thisSum);
    }
    
}