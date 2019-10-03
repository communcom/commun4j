package io.golos.commun4j.model;

import java.util.Date;
import java.util.List;

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-29.
 */
public class UserProfile {
    private String account_name;

    private int head_block_num;
    private Date head_block_time;
    private boolean privileged;
    private Date last_code_update;
    private Date created;
    private String core_liquid_balance;
    private long ram_quota;
    private long net_weight;
    private long cpu_weight;
    private long ram_usage;
    private List<AccountPermission> permissions;

    public UserProfile(String account_name, int head_block_num, Date head_block_time, boolean privileged, Date last_code_update, Date created, String core_liquid_balance, long ram_quota, long net_weight, long cpu_weight, long ram_usage, List<AccountPermission> permissions) {
        this.account_name = account_name;
        this.head_block_num = head_block_num;
        this.head_block_time = head_block_time;
        this.privileged = privileged;
        this.last_code_update = last_code_update;
        this.created = created;
        this.core_liquid_balance = core_liquid_balance;
        this.ram_quota = ram_quota;
        this.net_weight = net_weight;
        this.cpu_weight = cpu_weight;
        this.ram_usage = ram_usage;
        this.permissions = permissions;
    }

    public String getAccount_name() {
        return account_name;
    }

    public int getHead_block_num() {
        return head_block_num;
    }

    public Date getHead_block_time() {
        return head_block_time;
    }

    public boolean isPrivileged() {
        return privileged;
    }

    public Date getLast_code_update() {
        return last_code_update;
    }

    public Date getCreated() {
        return created;
    }

    public String getCore_liquid_balance() {
        return core_liquid_balance;
    }

    public long getRam_quota() {
        return ram_quota;
    }

    public long getNet_weight() {
        return net_weight;
    }

    public long getCpu_weight() {
        return cpu_weight;
    }

    public long getRam_usage() {
        return ram_usage;
    }

    public List<AccountPermission> getPermissions() {
        return permissions;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "account_name='" + account_name + '\'' +
                ", head_block_num=" + head_block_num +
                ", head_block_time=" + head_block_time +
                ", privileged=" + privileged +
                ", last_code_update=" + last_code_update +
                ", created=" + created +
                ", core_liquid_balance='" + core_liquid_balance + '\'' +
                ", ram_quota=" + ram_quota +
                ", net_weight=" + net_weight +
                ", cpu_weight=" + cpu_weight +
                ", ram_usage=" + ram_usage +
                ", permissions=" + permissions +
                '}';
    }

    public   static class AccountPermission {
        private String perm_name;
        private String parent;
        private AccountRequiredAuth required_auth;

        public AccountPermission(String perm_name, String parent, AccountRequiredAuth required_auth) {
            this.perm_name = perm_name;
            this.parent = parent;
            this.required_auth = required_auth;
        }

        public String getPerm_name() {
            return perm_name;
        }

        public String getParent() {
            return parent;
        }

        public AccountRequiredAuth getRequired_auth() {
            return required_auth;
        }

        @Override
        public String toString() {
            return "AccountPermission{" +
                    "perm_name='" + perm_name + '\'' +
                    ", parent='" + parent + '\'' +
                    ", required_auth=" + required_auth +
                    '}';
        }
    }

    public static class AccountRequiredAuth {
        private int threshold;
        private List<AccountKey> keys;
        private List<AccountAuth> accounts;

        public AccountRequiredAuth(int threshold, List<AccountKey> keys, List<AccountAuth> accounts) {
            this.threshold = threshold;
            this.keys = keys;
            this.accounts = accounts;
        }

        public int getThreshold() {
            return threshold;
        }

        public List<AccountKey> getKeys() {
            return keys;
        }

        public List<AccountAuth> getAccounts() {
            return accounts;
        }

        @Override
        public String toString() {
            return "AccountRequiredAuth{" +
                    "threshold=" + threshold +
                    ", keys=" + keys +
                    ", accounts=" + accounts +
                    '}';
        }
    }

    public static class AccountKey {
        private String key;
        private Short weight;

        public AccountKey(String key, Short weight) {
            this.key = key;
            this.weight = weight;
        }

        public String getKey() {
            return key;
        }

        public Short getWeight() {
            return weight;
        }

        @Override
        public String toString() {
            return "AccountKey{" +
                    "key='" + key + '\'' +
                    ", weight=" + weight +
                    '}';
        }
    }

    public static class AccountAuth {
        private AccountAuthPermission permission;
        private int weight;

        public AccountAuth(AccountAuthPermission permission, int weight) {
            this.permission = permission;
            this.weight = weight;
        }

        public AccountAuthPermission getPermission() {
            return permission;
        }

        public int getWeight() {
            return weight;
        }

        @Override
        public String toString() {
            return "AccountAuth{" +
                    "permission=" + permission +
                    ", weight=" + weight +
                    '}';
        }
    }

    public static class AccountAuthPermission {
        private String actor;
        private String permission;

        public AccountAuthPermission(String actor, String permission) {
            this.actor = actor;
            this.permission = permission;
        }

        public String getActor() {
            return actor;
        }

        public String getPermission() {
            return permission;
        }

        @Override
        public String toString() {
            return "AccountAuthPermission{" +
                    "actor='" + actor + '\'' +
                    ", permission='" + permission + '\'' +
                    '}';
        }
    }
}
