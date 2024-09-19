
# Security

Basic concepts of security in web systems are authorization and authentication.
In simple terms, Authentication is the process of verifying who a user is, while authorization is the process of verifying what they have access to.

* Authentication -(uwierzytelnianie, poświadczenie)  - verifies the identity of a user or service
* Authorization - (upoważnienie, uprawnienie, zezwolenie) - determines theis access rights


# HTTPS
Encrypted HTTP protocol using TLS or SSL.
TLS (Transport Layer Security) protocol secures communication by using what's known as an asymmetric public key infrastructure.
This type of security system uses two different keys to encrypt communication between parties:
1. The private key - this key is controlled by the owner of the website and it's kept private
, as the reader may have speculated. This key lives on the web server and is used to decrypt information encrypted by the public key.
2. The public key - this key is available to everyone who wants to interact with the server.
Information that's encrypted by the public key can only be decrypted by the private key.

Communication between Client and the Server under HTTPS in TLS 1.2 
1. TCP Handshake
2. Client Sends message to server with following details: TLS version, supported cipher suite
3. Server sends Certificate that contains many details, one of it is a public key.
Public Key is used for communication between client and the server.
Message Encrypted with public key can only be decrypted with private key. 
It is called public key  asymmetric encryption.
4. Once Client have public key its generates session key (encrypted session key)
encrypt it with server public key and send to the server.
Since server has private key, only server can read(decrypt) encrypted session key fom the client.
5. From now on, Data transmission is done using encrypted session key and agreed cipher suites. 

Latest version of TLS (TLS1.3) uses one network round trip instead of 2 (TLS 1.2)
Its optimizes network round trips to 1.
Core concept of TLS 1.2 is still valid 

### Certificate signed with Own RootCA (Authority)
Following instruction shows how to create server certificate signed by Custom Root Certificate Authority(CA)

1. Generate private key
openssl genrsa -aes-256-cbc -out api-gateway-web-server-private.key 2048
private key password is the same as the key name including key extension.

2. Create certificate Signing Reqiest
openssl req -key api-gateway-web-server-private.key -new -out api-gateway-web-server.csr

3. Creating Self Signed Certificate
openssl x509 -signkey api-gateway-web-server-private.key -in api-gateway-web-server.csr -req -days 365 -out api-gateway-web-server.crt

4. Create Self Signed Root CA
Lets create private key (rootCA.key) and self signed rootCA certificate (rootCA.crt)
openssl req -x509 -sha256 -days 1825 -newkey rsa:2048 -keyout rootCA.key -out rootCA.crt

5. Create configuration .ext file domain.ext

authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
subjectAltName = @alt_names
[alt_names]
DNS.1 = localhost

Sign CSR(api-gateway-web-server.csr) with root CA and its private key
As a result CA-signed certificate will be in the api-gateway-web-server.crt
openssl x509 -req -CA rootCA.crt -CAkey rootCA.key -in api-gateway-web-server.csr -out api-gateway-web-server.crt -days 365 -CAcreateserial -extfile domain.ext


After creating RootCA, add you Certificate Authority to the list of trusted authorities in your web browser.
Full instruction can be found here:
https://www.baeldung.com/openssl-self-signed-cert


# OAuth2.0 
Protocol that allows user to grant a third-party website or applications
to the user protected resources without necessarily revealing 
long term credentials or even their identity.

In oauth we specify following terminology:
* **Resource owner**: Entity that can gran access to the protected resource. Typically, this is an end user.
* **Resource server**: Server hosting protected resources. The API you want to access.
* **Client: Application** requesting access to a protected resource on behalf of the resource owner.
* **Authorization server**: Server that authenticates the resource owner and issue access token after getting proper authorization.

Authorization flow - Grant types:
* Authorization Code flow:
* Implicit Flow
* Resource owner password flow 
* Client credentials flow

oauth2.0 documentation:
* https://auth0.com/docs/authenticate/protocols/oauth
* https://www.oauth.com/playground/
* OAuth2.0 spring boot and resource server configuration:
https://docs.spring.io/spring-security/reference/reactive/oauth2/resource-server/index.html

# Microservices

## Circuit breaker - Resilence4J

We are using it e.g in case of errors in communication between services.</br>
Circuit breaker have three default states: Closed, Open, Half Open</br>
Closed means that there is no issue in between and standard flow of communication is performed.</br>
Whenever error occurred (e.g our dependent service is down)  circuit breaker gets into Open state.</br>
In the open state we will not call the service which is down until it is up again or some other cases are fulfilled(dependet from configuration).</br>
Depend on configuration  
You can read more here: https://resilience4j.readme.io/docs/circuitbreaker</br>

