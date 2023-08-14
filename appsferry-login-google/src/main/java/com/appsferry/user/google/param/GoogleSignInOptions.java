package com.appsferry.user.google.param;

import java.util.List;

import com.appsferry.user.google.common.SignInType;

public class GoogleSignInOptions {
    @SignInType
    public String signInOption;
    public List<String> requestedScopes;
    public String hostedDomain;
    public String clientId;


    public static final class Builder {
        private String signInOption;
        private List<String> requestedScopes;
        private String hostedDomain;
        private String clientId;

        public Builder() {

        }

        public Builder setSignInOption(@SignInType String signInOption) {
            this.signInOption = signInOption;
            return this;
        }

        public Builder setRequestedScopes(List<String> requestedScopes) {
            this.requestedScopes = requestedScopes;
            return this;
        }

        public Builder setHostedDomain(String hostedDomain) {
            this.hostedDomain = hostedDomain;
            return this;
        }

        public Builder setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public GoogleSignInOptions build() {
            GoogleSignInOptions options = new GoogleSignInOptions();
            options.signInOption = signInOption;
            options.requestedScopes = requestedScopes;
            options.hostedDomain = hostedDomain;
            options.clientId = clientId;
            return options;
        }
    }

}
