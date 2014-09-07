/**
 * USAddressVerificationSoap_BindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.strikeiron.ws.usaddress;

public class USAddressVerificationSoap_BindingStub extends org.apache.axis.client.Stub implements com.apatar.strikeiron.ws.usaddress.USAddressVerificationSoap_PortType {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[3];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("VerifyAddressUSA");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UnregisteredUserEmail"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UserID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "Password"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "addressLine1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "addressLine2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "city_state_zip"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "firm"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "urbanization"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "casing"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.strikeiron.com", "casingEnum"), com.apatar.strikeiron.ws.usaddress.CasingEnum.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "VerifyAddressUSAResult"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.strikeiron.com", "USAddress"), com.apatar.strikeiron.ws.usaddress.USAddress.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"), com.apatar.strikeiron.ws.usaddress.SISubscriptionInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("VerifyAddressUSABatch");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UnregisteredUserEmail"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UserID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "Password"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "address"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.strikeiron.com", "ArrayOfUSBatchAddress"), com.apatar.strikeiron.ws.usaddress.ArrayOfUSBatchAddress.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "casing"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.strikeiron.com", "casingEnum"), com.apatar.strikeiron.ws.usaddress.CasingEnum.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "VerifyAddressUSABatchResult"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.strikeiron.com", "ArrayOfUSAddress"), com.apatar.strikeiron.ws.usaddress.ArrayOfUSAddress.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"), com.apatar.strikeiron.ws.usaddress.SISubscriptionInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetRemainingHits");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UnregisteredUserEmail"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UserID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "Password"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseStatusCode"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseStatus"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseActionCode"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseAction"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "RemainingHits"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "Amount"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"), java.math.BigDecimal.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

    }

    public USAddressVerificationSoap_BindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public USAddressVerificationSoap_BindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public USAddressVerificationSoap_BindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "Address");
            cachedSerQNames.add(qName);
            cls = com.apatar.strikeiron.ws.usaddress.Address.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "ArrayOfUSAddress");
            cachedSerQNames.add(qName);
            cls = com.apatar.strikeiron.ws.usaddress.ArrayOfUSAddress.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "ArrayOfUSBatchAddress");
            cachedSerQNames.add(qName);
            cls = com.apatar.strikeiron.ws.usaddress.ArrayOfUSBatchAddress.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "casingEnum");
            cachedSerQNames.add(qName);
            cls = com.apatar.strikeiron.ws.usaddress.CasingEnum.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "GeoCode");
            cachedSerQNames.add(qName);
            cls = com.apatar.strikeiron.ws.usaddress.GeoCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo");
            cachedSerQNames.add(qName);
            cls = com.apatar.strikeiron.ws.usaddress.SISubscriptionInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "USAddress");
            cachedSerQNames.add(qName);
            cls = com.apatar.strikeiron.ws.usaddress.USAddress.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "USBatchAddress");
            cachedSerQNames.add(qName);
            cls = com.apatar.strikeiron.ws.usaddress.USBatchAddress.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "USGeoCode");
            cachedSerQNames.add(qName);
            cls = com.apatar.strikeiron.ws.usaddress.USGeoCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public void verifyAddressUSA(java.lang.String unregisteredUserEmail, java.lang.String userID, java.lang.String password, java.lang.String addressLine1, java.lang.String addressLine2, java.lang.String city_state_zip, java.lang.String firm, java.lang.String urbanization, com.apatar.strikeiron.ws.usaddress.CasingEnum casing, com.apatar.strikeiron.ws.usaddress.holders.USAddressHolder verifyAddressUSAResult, com.apatar.strikeiron.ws.usaddress.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.strikeiron.com/VerifyAddressUSA");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.strikeiron.com", "VerifyAddressUSA"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {unregisteredUserEmail, userID, password, addressLine1, addressLine2, city_state_zip, firm, urbanization, casing});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            java.util.Map _output;
            _output = _call.getOutputParams();
            try {
                verifyAddressUSAResult.value = (com.apatar.strikeiron.ws.usaddress.USAddress) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "VerifyAddressUSAResult"));
            } catch (java.lang.Exception _exception) {
                verifyAddressUSAResult.value = (com.apatar.strikeiron.ws.usaddress.USAddress) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "VerifyAddressUSAResult")), com.apatar.strikeiron.ws.usaddress.USAddress.class);
            }
            try {
                SISubscriptionInfo.value = (com.apatar.strikeiron.ws.usaddress.SISubscriptionInfo) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"));
            } catch (java.lang.Exception _exception) {
                SISubscriptionInfo.value = (com.apatar.strikeiron.ws.usaddress.SISubscriptionInfo) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo")), com.apatar.strikeiron.ws.usaddress.SISubscriptionInfo.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void verifyAddressUSABatch(java.lang.String unregisteredUserEmail, java.lang.String userID, java.lang.String password, com.apatar.strikeiron.ws.usaddress.ArrayOfUSBatchAddress address, com.apatar.strikeiron.ws.usaddress.CasingEnum casing, com.apatar.strikeiron.ws.usaddress.holders.ArrayOfUSAddressHolder verifyAddressUSABatchResult, com.apatar.strikeiron.ws.usaddress.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.strikeiron.com/VerifyAddressUSABatch");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.strikeiron.com", "VerifyAddressUSABatch"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {unregisteredUserEmail, userID, password, address, casing});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            java.util.Map _output;
            _output = _call.getOutputParams();
            try {
                verifyAddressUSABatchResult.value = (com.apatar.strikeiron.ws.usaddress.ArrayOfUSAddress) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "VerifyAddressUSABatchResult"));
            } catch (java.lang.Exception _exception) {
                verifyAddressUSABatchResult.value = (com.apatar.strikeiron.ws.usaddress.ArrayOfUSAddress) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "VerifyAddressUSABatchResult")), com.apatar.strikeiron.ws.usaddress.ArrayOfUSAddress.class);
            }
            try {
                SISubscriptionInfo.value = (com.apatar.strikeiron.ws.usaddress.SISubscriptionInfo) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"));
            } catch (java.lang.Exception _exception) {
                SISubscriptionInfo.value = (com.apatar.strikeiron.ws.usaddress.SISubscriptionInfo) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo")), com.apatar.strikeiron.ws.usaddress.SISubscriptionInfo.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void getRemainingHits(java.lang.String unregisteredUserEmail, java.lang.String userID, java.lang.String password, javax.xml.rpc.holders.IntHolder licenseStatusCode, javax.xml.rpc.holders.StringHolder licenseStatus, javax.xml.rpc.holders.IntHolder licenseActionCode, javax.xml.rpc.holders.StringHolder licenseAction, javax.xml.rpc.holders.IntHolder remainingHits, javax.xml.rpc.holders.BigDecimalHolder amount) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://wsparam.strikeiron.com/StrikeIron/USAddressVerification4_0/USAddressVerification/GetRemainingHits");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.strikeiron.com", "SILicenseInfo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {unregisteredUserEmail, userID, password});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            java.util.Map _output;
            _output = _call.getOutputParams();
            try {
                licenseStatusCode.value = ((java.lang.Integer) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseStatusCode"))).intValue();
            } catch (java.lang.Exception _exception) {
                licenseStatusCode.value = ((java.lang.Integer) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseStatusCode")), int.class)).intValue();
            }
            try {
                licenseStatus.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseStatus"));
            } catch (java.lang.Exception _exception) {
                licenseStatus.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseStatus")), java.lang.String.class);
            }
            try {
                licenseActionCode.value = ((java.lang.Integer) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseActionCode"))).intValue();
            } catch (java.lang.Exception _exception) {
                licenseActionCode.value = ((java.lang.Integer) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseActionCode")), int.class)).intValue();
            }
            try {
                licenseAction.value = (java.lang.String) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseAction"));
            } catch (java.lang.Exception _exception) {
                licenseAction.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseAction")), java.lang.String.class);
            }
            try {
                remainingHits.value = ((java.lang.Integer) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "RemainingHits"))).intValue();
            } catch (java.lang.Exception _exception) {
                remainingHits.value = ((java.lang.Integer) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "RemainingHits")), int.class)).intValue();
            }
            try {
                amount.value = (java.math.BigDecimal) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "Amount"));
            } catch (java.lang.Exception _exception) {
                amount.value = (java.math.BigDecimal) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "Amount")), java.math.BigDecimal.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
