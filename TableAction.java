/*
    Paulo Vasconcelos
    paulobvasconcelos@gmail.com
    2018 july
 */

public abstract class TableAction {

    // ----------------------------------------
    // Atributes
    public String name    = null;
    public Integer amount = null;
    public Player target  = null;

    // ----------------------------------------
    // Constructors
    public TableAction() { }

    public TableAction(String name) {
        this.name = name;
    }

    public TableAction(String name, Integer amount) {
        this.name = name;
        this.amount = amount;
    }

    public TableAction(String name, Player target) {
        this.name = name;
        this.target = target;
    }

    public TableAction(String name, Integer amount, Player target) {
        this.name = name;
        this.amount = amount;
        this.target = target;
    }

    // ----------------------------------------
    // Functions
    public String getName()     { return this.name; }
    public Integer getAmount()  { return this.amount; }
    public Player getTarget()   { return this.target; }
    public void setName(String name)        { this.name = name; }
    public void setAmount(Integer amount)   { this.amount = amount; }
    public void setTarget(Player target)    { this.target = target; }
    // ----------------------------------------
}