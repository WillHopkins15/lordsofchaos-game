package org.lordsofchaos.matrixobjects;

import org.lordsofchaos.coordinatesystems.MatrixCoordinates;

public abstract class MatrixObject implements Comparable<MatrixObject>
{
    // this is not a real-world position, so when constructing a matrixObject, only pass in
    // matrixPositions
    protected MatrixCoordinates matrixPosition;
    
    public MatrixObject(int x, int y) {
        setMatrixPosition(x, y);
    }
    
    public void setMatrixPosition(int x, int y) {
        matrixPosition = new MatrixCoordinates(x, y);
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