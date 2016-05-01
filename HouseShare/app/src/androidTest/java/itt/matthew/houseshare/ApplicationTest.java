package itt.matthew.houseshare;

import android.app.Application;
import android.bluetooth.BluetoothAssignedNumbers;
import android.os.Bundle;
import android.test.ApplicationTestCase;
import android.util.Log;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableQueryCallback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.http.StatusLine;

import junit.framework.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import itt.matthew.houseshare.Fragments.PersonalCostOverview;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.CostCategory;
import itt.matthew.houseshare.Models.CostSplit;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.Models.Task;
import itt.matthew.houseshare.Models.TaskArea;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testHouseParse() throws Exception {

        House h = new House("Test", "Test");
        h.setHouseID(0);

        Bundle b = new Bundle();
        b.putParcelable("house", h);

        assertSame(h, b.getParcelable("house"));

    }


    public void testCostNumIntervals() throws Exception{
        House house = new House("Test", "Test");
        Account account = new Account("0", "Matthew Roche", "M.Roche@gmail.com", "URL");
        Account account2 = new Account("1", "John Doe", "J.Doe@gmail.com", "URL");
        ArrayList<Account> members = new ArrayList<>();
        members.add(account);
        members.add(account2);
        house.setMembers(members);

        Cost c = new Cost();
        c.setAmount(100);
        ArrayList<CostSplit> costSplits = new ArrayList<>();
        for (int i = 0; i < house.getMembers().size(); i++){
            costSplits.add(new CostSplit(house.getMembers().get(i).getFacebookID(), house.getMembers().get(i).getName(), 50, false));
        }
        c.setSplit(costSplits);
        c.setInterval(1);
        c.setCategory(new CostCategory("0", 0));
        c.setStartDate(Calendar.getInstance());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 20);
        c.setEndDate(cal);
        c.initalizeIntervals();

        assertSame(c.getIntervals().size(), 20);
    }

    public void testRotation() throws Exception{

        House house = new House("Test", "Test");
        Account account = new Account("0", "Matthew Roche", "M.Roche@gmail.com", "URL");
        Account account2 = new Account("1", "John Doe", "J.Doe@gmail.com", "URL");
        ArrayList<Account> members = new ArrayList<>();
        members.add(account);
        members.add(account2);
        house.setMembers(members);

        Task t= new Task();

        t.setArea(new TaskArea("Test", "Test", 0));
        t.setInterval(1);

        ArrayList<String> users = new ArrayList<>();

        for (int i = 0; i < house.getMembers().size(); i++){
            users.add(house.getMembers().get(i).getFacebookID());
        }

        t.setUsers(users);
        t.setStartDate(Calendar.getInstance());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 20);
        t.setEndDate(cal);
        t.initalizeIntervals();

        assertEquals(t.getTaskInstances().get(0).getAccount(), "0");
        assertEquals(t.getTaskInstances().get(1).getAccount(), "1");
        assertEquals(t.getTaskInstances().get(2).getAccount(), "0");
        assertEquals(t.getTaskInstances().get(3).getAccount(), "1");
        assertEquals(t.getTaskInstances().get(4).getAccount(), "0");
        assertEquals(t.getTaskInstances().get(5).getAccount(), "1");
        assertEquals(t.getTaskInstances().get(6).getAccount(), "0");
        assertEquals(t.getTaskInstances().get(7).getAccount(), "1");
        assertEquals(t.getTaskInstances().get(8).getAccount(), "0");
        assertEquals(t.getTaskInstances().get(9).getAccount(), "1");
    }

    public void testAccountParse() throws Exception {

        Account account = new Account("0", "Matthew Roche", "M.Roche@gmail.com", "URL");

        Bundle b = new Bundle();
        b.putParcelable("account", account);

        assertSame(account, b.getParcelable("account"));
    }

    public void testIntervalsCost() throws Exception{

        House house = new House("Test", "Test");
        Account account = new Account("0", "Matthew Roche", "M.Roche@gmail.com", "URL");
        Account account2 = new Account("1", "John Doe", "J.Doe@gmail.com", "URL");
        ArrayList<Account> members = new ArrayList<>();
        members.add(account);
        members.add(account2);
        house.setMembers(members);

        Cost c = new Cost();
        c.setAmount(100);
        ArrayList<CostSplit> costSplits = new ArrayList<>();
        for (int i = 0; i < house.getMembers().size(); i++){
            costSplits.add(new CostSplit(house.getMembers().get(i).getFacebookID(), house.getMembers().get(i).getName(), 50, false));
        }
        c.setSplit(costSplits);
        c.setInterval(5);
        c.setCategory(new CostCategory("0", 0));
        c.setStartDate(Calendar.getInstance());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 20);
        c.setEndDate(cal);
        c.initalizeIntervals();

        Calendar intervalAdd = c.getStartDate();
        intervalAdd.add(Calendar.DATE, c.getInterval());

        assertSame(c.getIntervals().get(0).getDate().get(Calendar.DATE), intervalAdd.get(Calendar.DATE));

    }


    public void testIntervalsTask() throws Exception{

        House house = new House("Test", "Test");
        Account account = new Account("0", "Matthew Roche", "M.Roche@gmail.com", "URL");
        Account account2 = new Account("1", "John Doe", "J.Doe@gmail.com", "URL");
        ArrayList<Account> members = new ArrayList<>();
        members.add(account);
        members.add(account2);
        house.setMembers(members);

        Task t= new Task();

        t.setArea(new TaskArea("Test", "Test", 0));
        t.setInterval(5);

        ArrayList<String> users = new ArrayList<>();

        for (int i = 0; i < house.getMembers().size(); i++){
            users.add(house.getMembers().get(i).getFacebookID());
        }

        t.setUsers(users);
        t.setStartDate(Calendar.getInstance());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 20);
        t.setEndDate(cal);
        t.initalizeIntervals();

        Calendar intervalAdd = t.getStartDate();
        intervalAdd.add(Calendar.DATE, t.getInterval());

        assertSame(t.getTaskInstances().get(0).getDate().get(Calendar.DATE), intervalAdd.get(Calendar.DATE));

    }

    public void testCostSplit() throws Exception{
        House house = new House("Test", "Test");
        Account account = new Account("0", "Matthew Roche", "M.Roche@gmail.com", "URL");
        Account account2 = new Account("1", "John Doe", "J.Doe@gmail.com", "URL");
        ArrayList<Account> members = new ArrayList<>();
        members.add(account);
        members.add(account2);
        house.setMembers(members);

        Cost c = new Cost();
        c.setAmount(100);
        ArrayList<CostSplit> costSplits = new ArrayList<>();
        for (int i = 0; i < house.getMembers().size(); i++){
            costSplits.add(new CostSplit(house.getMembers().get(i).getFacebookID(), house.getMembers().get(i).getName(), 50, false));
        }
        c.setSplit(costSplits);
        c.setInterval(5);
        c.setCategory(new CostCategory("0", 0));
        c.setStartDate(Calendar.getInstance());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 20);
        c.setEndDate(cal);
        c.initalizeIntervals();

        Calendar intervalAdd = c.getStartDate();
        intervalAdd.add(Calendar.DATE, c.getInterval());

        Double costCount = 0.0;

        for (int i = 0; i < costSplits.size(); i++){
            costCount += costSplits.get(i).getAmount();
        }

        assertEquals(costCount, c.getAmount());
    }

    public void testTaskNumIntervals() throws Exception{

        House house = new House("Test", "Test");
        Account account = new Account("0", "Matthew Roche", "M.Roche@gmail.com", "URL");
        Account account2 = new Account("1", "John Doe", "J.Doe@gmail.com", "URL");
        ArrayList<Account> members = new ArrayList<>();
        members.add(account);
        members.add(account2);
        house.setMembers(members);

        Task t= new Task();

        t.setArea(new TaskArea("Test", "Test", 0));
        t.setInterval(1);

        ArrayList<String> users = new ArrayList<>();

        for (int i = 0; i < house.getMembers().size(); i++){
            users.add(house.getMembers().get(i).getFacebookID());
        }

        t.setUsers(users);
        t.setStartDate(Calendar.getInstance());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 20);
        t.setEndDate(cal);
        t.initalizeIntervals();

        assertEquals(t.getTaskInstances().size(), 20);

    }


    public void testReorderedRotation(){
        House house = new House("Test", "Test");
        Account account = new Account("0", "Matthew Roche", "M.Roche@gmail.com", "URL");
        Account account2 = new Account("1", "John Doe", "J.Doe@gmail.com", "URL");
        ArrayList<Account> members = new ArrayList<>();
        members.add(account);
        members.add(account2);
        house.setMembers(members);

        Task t= new Task();

        t.setArea(new TaskArea("Test", "Test", 0));
        t.setInterval(1);

        ArrayList<String> users = new ArrayList<>();

        for (int i = house.getMembers().size() -1; i >= 0; i--){
            users.add(house.getMembers().get(i).getFacebookID());
        }

        t.setUsers(users);
        t.setStartDate(Calendar.getInstance());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 20);
        t.setEndDate(cal);
        t.initalizeIntervals();

        assertEquals(t.getTaskInstances().get(0).getAccount(), "1");
        assertEquals(t.getTaskInstances().get(1).getAccount(), "0");
        assertEquals(t.getTaskInstances().get(2).getAccount(), "1");
        assertEquals(t.getTaskInstances().get(3).getAccount(), "0");
        assertEquals(t.getTaskInstances().get(4).getAccount(), "1");
        assertEquals(t.getTaskInstances().get(5).getAccount(), "0");
        assertEquals(t.getTaskInstances().get(6).getAccount(), "1");
        assertEquals(t.getTaskInstances().get(7).getAccount(), "0");
        assertEquals(t.getTaskInstances().get(8).getAccount(), "1");
        assertEquals(t.getTaskInstances().get(9).getAccount(), "0");
    }


    public void testAzureStatus() throws Exception {

        final OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://backendhs.azurewebsites.net")
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()){
                fail();
                throw new IOException("Unexpected code " + response);
            }

            Headers responseHeaders = response.headers();
            for (int i = 0; i < responseHeaders.size(); i++) {
                System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
            }
        }

}