package org.lordsofchaos;

import java.util.ArrayList;
import java.util.List;

public class Path extends MatrixObject
{
    private List<Troop> troops;
    
    public Path(int y, int x, List<Troop> troops)
    {
        super(y, x);
        SetTroops(troops);
    }
    
    // Getters and Setters
    public List<Troop> GetTroops()
    {
        if (troops == null)
        {
            troops = new ArrayList<Troop>();
        }
        return troops;
    }
    
    public void SetTroops(List<Troop> troops)
    {
        this.troops = troops;
    }
    //
}
