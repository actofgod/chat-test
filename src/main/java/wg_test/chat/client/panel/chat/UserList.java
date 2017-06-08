package wg_test.chat.client.panel.chat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import wg_test.chat.client.entity.UserImpl;

import java.util.HashMap;
import java.util.List;

public class UserList
{
    private ListView<UserImpl> list;
    private ObservableList<UserImpl> data;
    private HashMap<Integer, UserImpl> allUsers;
    private UserImpl general;

    public UserList()
    {
        list = new ListView<>();
        general = new UserImpl(0, "general");
        data = FXCollections.observableArrayList(general);
        list.setItems(data);
        allUsers = new HashMap<>();
        allUsers.put(general.getId(), general);
    }

    public void setUserList(List<UserImpl> users)
    {
        data.clear();
        allUsers.clear();
        data.add(general);
        allUsers.put(general.getId(), general);
        data.addAll(users);
        for (UserImpl u : users) {
            allUsers.put(u.getId(), u);
        }
    }

    public ListView<UserImpl> getListView()
    {
        return list;
    }

    public UserImpl getGeneralUser()
    {
        return general;
    }

    public HashMap<Integer, UserImpl> getAllUsers()
    {
        return allUsers;
    }

    public void updateUserStatus(int userId, boolean online)
    {
        for (UserImpl user: data) {
            if (user.getId() == userId) {
                if (user.isOnline() != online) {
                    user.setIsOnline(online);
                    list.refresh();
                }
                return;
            }
        }
    }
}
