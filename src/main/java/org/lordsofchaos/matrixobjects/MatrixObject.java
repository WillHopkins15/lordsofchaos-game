package org.lordsofchaos.matrixobjects;

import org.lordsofchaos.coordinatesystems.MatrixCoordinates;

public abstract class MatrixObject {
    // this is not a real-world position, so when constructing a matrixObject, only pass in
    // matrixPositions
    protected MatrixCoordinates matrixPosition;
    
    public MatrixObject(int y, int x)
    {
        setMatrixPosition(y, x);
    }
    
    public void setMatrixPosition(int y, int x)
    {
        matrixPosition = new MatrixCoordinates(y, x);
    }
    
    public MatrixCoordinates getMatrixPosition()
    {
        return matrixPosition;
    }
}
