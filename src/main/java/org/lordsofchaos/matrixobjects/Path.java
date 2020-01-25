package org.lordsofchaos.matrixobjects;

import java.util.ArrayList;
import java.util.List;

import org.lordsofchaos.gameobjects.troops.Troop;

public class Path extends MatrixObject
{
    private List<Troop> troops;
    
    public Path(int y, int x, List<Troop> troops)
    {
        super(y, x);
        setTroops(troops);
    }
    
    // Getters and Setters
    public List<Troop> getTroops()
    {
        if (troops == null)
        {
            troops = new ArrayList<Troop>();
        }
        return troops;
    }
    
    public void setTroops(List<Troop> troops)
    {
        this.troops = troops;
    }
    //
    
    public void addTroop(Troop troop)
    {
    	getTroops().add(troop);
    }
    
    public void removeTroop(Troop troop)
    {
    	for (int i = 0; i < getTroops().size(); i++)
    	{
    		
    	}
    }
}
