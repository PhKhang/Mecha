package com.example.mechaadmin;

import java.util.List;

public class GroupChat {
    private String groupChatName;
    private String creationDate;
    private List<Member> members;

    public static class Member {
        private String name;
        private boolean isAdmin;

        public Member(String name, boolean isAdmin) {
            this.name = name;
            this.isAdmin = isAdmin;
        }

        public String getName() {
            return name;
        }

        public boolean isAdmin() {
            return isAdmin;
        }
    }

    public GroupChat(String groupChatName, String creationDate) {
        this.groupChatName = groupChatName;
        this.creationDate = creationDate;
        this.members = List.of(
                new Member("Trần Nguyễn Phúc Khang", true),
                new Member("Lê Trí Mẩn", false));
    }
    
    public String getGroupChatName() {
        return groupChatName;
    }
    
    public String getCreationDate() {
        return creationDate;
    }
    
    public List<Member> getMembers() {
        return members;
    }
    
    public String getMemNames() {
        return String.join(", ", members.stream().map(Member::getName).toArray(String[]::new));
    }
}
