package io.golos.commun4j.model;

import java.util.List;

public class AccountCreationResult {

    private String creator;
    private String name;
    private AccountAuthorizations owner;
    private AccountAuthorizations active;

    public AccountCreationResult(String creator, String name, AccountAuthorizations owner, AccountAuthorizations active) {
        this.creator = creator;
        this.name = name;
        this.owner = owner;
        this.active = active;
    }

    public AccountCreationResult() {
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountAuthorizations getOwner() {
        return owner;
    }

    public void setOwner(AccountAuthorizations owner) {
        this.owner = owner;
    }

    public AccountAuthorizations getActive() {
        return active;
    }

    public void setActive(AccountAuthorizations active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "AccountCreationResult{" +
                "creator='" + creator + '\'' +
                ", name='" + name + '\'' +
                ", owner=" + owner +
                ", active=" + active +
                '}';
    }

    static class AccountAuthorizations {
        private int threshold;
        private List<AccountKey> keys;
        private List<String> accounts;
        private List<String> waits;

        public AccountAuthorizations(int threshold, List<AccountKey> keys, List<String> accounts, List<String> waits) {
            this.threshold = threshold;
            this.keys = keys;
            this.accounts = accounts;
            this.waits = waits;
        }

        public AccountAuthorizations() {
        }

        public int getThreshold() {
            return threshold;
        }

        public void setThreshold(int threshold) {
            this.threshold = threshold;
        }

        public List<AccountKey> getKeys() {
            return keys;
        }

        public void setKeys(List<AccountKey> keys) {
            this.keys = keys;
        }

        public List<String> getAccounts() {
            return accounts;
        }

        public void setAccounts(List<String> accounts) {
            this.accounts = accounts;
        }

        public List<String> getWaits() {
            return waits;
        }

        public void setWaits(List<String> waits) {
            this.waits = waits;
        }

        @Override
        public String toString() {
            return "AccountAuthorizations{" +
                    "threshold=" + threshold +
                    ", keys=" + keys +
                    ", accounts=" + accounts +
                    ", waits=" + waits +
                    '}';
        }
    }

    static class AccountKey {

        private String key;
        private int weight;

        public AccountKey(String key, int weight) {
            this.key = key;
            this.weight = weight;
        }

        public AccountKey() {
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "AccountKey{" +
                    "key='" + key + '\'' +
                    ", weight=" + weight +
                    '}';
        }
    }
}