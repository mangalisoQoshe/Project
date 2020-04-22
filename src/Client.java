import java.util.ArrayList;

class Client
{
    private String usrName,passWord;
    
    public Client(String usrName, String passWord)
    {
        this.usrName = usrName;
        this.passWord = passWord;
     }

    public String getName()
    {
        return usrName;
    }

    public String getPassWord()
    {
        return passWord;
    }
    
     
     public String toString()
    {
        return usrName+"_"+passWord;
    }

    public boolean equals(Client obj)
    {
        if(obj.getName().equalsIgnoreCase(this.getName()) && obj.getPassWord().equalsIgnoreCase(this.getPassWord()))
        {
            return true;
        }
        return false;
    }

}