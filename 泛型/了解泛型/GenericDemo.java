 
import java.util.ArrayList;

/**
 * Created by asd on 2016/9/13.
 */
/*
    泛型的由来:
        1:我们往集合中添加了String和Integer类型
        2:遍历时,我们对元素进行强转,当成String类型,Integer不能转成是String的,所以报错了
        3:这个错误在编译时期没有告诉我们,我就觉得设计得不好
        4:数组在添加其他类型的元素时,编译时期就有问题了
        5:集合也模仿着数据,在创建对象的时候明确元素的数据类型,就不会再有问题了
        6:这种技术就被称为泛型

    泛型:
        把类型明确的工作推迟到创建对象或调用方法的时候才去明确的特殊的类型

    参数化类型:
        1:把类型当作是参数一样的传递
        2:<数据类型>只能是引用类型


    好处:
        1:把运行时期的问题提前到了编译时期
        2:避免了强制类型转换
        3:优化了程序设计

 */
public class GenericDemo {
    public static void main(String[] args) {
        //创建集合对象
        ArrayList<String> list = new ArrayList<>();

        list.add("hello");
        list.add("world");
        list.add("java");

        //遍历,由于明确了类型.我们可以增强for
        for (String s : list) {
            System.out.println(s);
        }
    }
}
