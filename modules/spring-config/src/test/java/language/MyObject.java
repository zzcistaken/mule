package language;

import org.mule.tck.testmodels.fruit.Banana;

import javax.inject.Inject;

/**
 * Created by pablolagreca on 1/8/16.
 */
public class MyObject
{
    private Banana banana;

    @Inject
    public void setBanana(Banana banana) {
        this.banana = banana;
    }

    public void init()
    {
        if (this.banana == null)
        {
            throw new RuntimeException("Banana not injectd");
        }
    }
}
