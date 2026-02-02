import Home from './pages/Home.jsx';
import Booking from './pages/Booking.jsx';
import BookingSuccess from './pages/BookingSuccess';
import MyAppointments from './pages/MyAppointments.jsx';
import AdminDashboard from './pages/AdminDashboard.jsx';
import AdminServices from './pages/AdminServices.jsx';
import AdminProfessionals from './pages/AdminProfessionals.jsx';
import AdminHours from './pages/AdminHours.jsx';
import AdminAppointments from './pages/AdminAppointments.jsx';
import AdminClients from './pages/AdminClients.jsx';
import __Layout from './Layout.jsx';


export const PAGES = {
    "Home": Home,
    "Booking": Booking,
    "BookingSuccess": BookingSuccess,
    "MyAppointments": MyAppointments,
    "AdminDashboard": AdminDashboard,
    "AdminServices": AdminServices,
    "AdminProfessionals": AdminProfessionals,
    "AdminHours": AdminHours,
    "AdminAppointments": AdminAppointments,
    "AdminClients": AdminClients,
}

export const pagesConfig = {
    mainPage: "Home",
    Pages: PAGES,
    Layout: __Layout,
};