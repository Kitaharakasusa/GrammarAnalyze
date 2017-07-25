import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import net.sf.json.JSONArray;
import org.apache.commons.lang.ObjectUtils;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        //System.out.println(HanLP.segment("今天的饭菜真的是好吃啊"));
        //CoNLLSentence sentence = HanLP.parseDependency("这家餐馆的牛肉味道独特");
        // 也可以直接拿到数组，任意顺序或逆序遍历

        for(int offset=2;offset<92;offset++)
        {
        String[] text=Search(offset*50000);
        for (int j=0;j<50000;j++) {

            System.out.println(text[j]);
            CoNLLSentence sentence= HanLP.parseDependency(text[j]);
            CoNLLWord[] wordArray = sentence.getWordArray();
            for (int i = wordArray.length - 1; i >= 0; i--) {
                CoNLLWord word = wordArray[i];
                if (word.DEPREL.equals("主谓关系")) {
                    String str = "['";
                    str += word.LEMMA;
                    str += "','";
                    str += word.HEAD.LEMMA;
                    str += "']";
                    System.out.println(str);
                    JSONArray obj=null;
                    try {
                        obj = JSONArray.fromObject(str);
                    }
                    catch (Exception e){}
                    method2("result.txt", obj);
                    method2("nature.txt",word.LEMMA);
                }

            }
        }
        System.gc();
        }
        // 还可以直接遍历子树，从某棵子树的某个节点一路遍历到虚根
    }
    public static void method2(String file,Object conent) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            out.write(conent+"\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public  static String[]Search(int offset)
    {
        String getrow="SELECT meituan_comments.\"text\" FROM \"public\".meituan_comments limit 50000 "+
                "offset "+Integer.toString(offset);
        ResultSet rs=Databaseoperator.DoGetResultSql(getrow);
        int rownum=0;
        try {
            if (rs.next()) {
                try {
                    System.out.println("buweikong");
                    rs.last();
                    rownum=rs.getRow();
                    rs.first();
                    rs.previous();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String resultarray[]=new String[rownum];
        System.out.print(rownum);
        for(int i=0;i<rownum;i++)
        {
            resultarray[i]=new String();
        }
        int count=0;
        try {
            while(rs.next())
            {
                String abbs;
                abbs=rs.getString(1);
                if(abbs == null || abbs.length() <= 0)continue;
                resultarray[count]=abbs;
                count++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultarray;

    }

}
