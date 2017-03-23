package controler.util;
/*<dependency>
    <groupId>net.sf.uadetector</groupId>
    <artifactId>uadetector-core</artifactId>
    <version>0.9.22</version>
</dependency>*/


import bean.Device;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

public class DeviceUtil {

    private static final DeviceUtil instance = new DeviceUtil();

    public static DeviceUtil getInstance() {
        return instance;
    }

    private DeviceUtil() {
    }

    private static ReadableUserAgent getUserAgent() {
        HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
        ReadableUserAgent agent = parser.parse(httpServletRequest.getHeader("User-Agent"));
        return agent;
    }

    public static Device getDevice() {
        ReadableUserAgent ag = getUserAgent();
        Device device = new Device();
        device.setBrowser(ag.getFamily().getName());
        device.setOperatingSystem(ag.getOperatingSystem().getName());
        device.setDeviceCategorie(ag.getDeviceCategory().getName());
        System.out.println("Device :: family :: " + ag.getFamily().getName());
        System.out.println("Operating système :: " + ag.getOperatingSystem().getName());
        System.out.println("Device cat :: " + ag.getDeviceCategory().getName());
        System.out.println("Systeme version number :: " + ag.getOperatingSystem().getVersionNumber());
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String userAgent = externalContext.getRequestHeaderMap().get("User-Agent");
        System.out.println("Header :: "+userAgent);
        return device;
    }

}
