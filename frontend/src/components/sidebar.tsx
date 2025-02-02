import {Card, CardHeader, CardTitle, CardDescription, CardContent, CardFooter} from './ui/card';
import {
    Sidebar,
    SidebarContent,
    SidebarGroup,
    SidebarGroupContent,
    SidebarGroupLabel,
} from '@/components/ui/sidebar';

export default function AppSidebar() {
    return (
        <Sidebar aria-hidden>
            <SidebarContent>
                <SidebarGroup>
                    <SidebarGroupLabel>Accessibility Issues</SidebarGroupLabel>
                    <SidebarGroupContent>
                        <Card>
                            <CardHeader>
                                <CardTitle>Card Title</CardTitle>
                                <CardDescription>Card Description</CardDescription>
                            </CardHeader>
                            <CardContent>
                                <p>Card Content</p>
                            </CardContent>
                            <CardFooter>
                                <p>Card Footer</p>
                            </CardFooter>
                        </Card>
                    </SidebarGroupContent>
                </SidebarGroup>
            </SidebarContent>
        </Sidebar>
    );
}
