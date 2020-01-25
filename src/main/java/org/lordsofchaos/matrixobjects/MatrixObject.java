package org.lordsofchaos.matrixobjects;

import org.lordsofchaos.Coordinates;

public abstract class MatrixObject {
    // this is not a real-world position, so when constructing a matrixObject, only pass in
    // matrixPositions
    protected Coordinates matrixPosition;
    
    public MatrixObject(int y, int x)
    {
        setMatrixPosition(y, x);
    }
    
    public void setMatrixPosition(int y, int x)
    {
        matrixPosition = new Coordinates(y, x);
    }
    
    public Coordinates getMatrixPosition()
    {
        return matrixPosition;
    }
}
