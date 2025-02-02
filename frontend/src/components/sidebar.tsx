import IssueCard from './issue-card';
import {
    Sidebar,
    SidebarContent,
    SidebarGroup,
    SidebarGroupContent,
    SidebarGroupLabel,
} from '@/components/ui/sidebar';

export default function AppSidebar({transforms, elements}: {transforms: Transform[], elements: HTMLElement[]}) {
    return (
        <Sidebar aria-hidden>
            <SidebarContent>
                <SidebarGroup>
                    <div className="flex flex-col min-h-[100vh]">
                        <SidebarGroupLabel>Accessibility Issues</SidebarGroupLabel>
                        <SidebarGroupContent>
                            {transforms.length === 0 ? (
                                <p className="flex justify-center items-center text-gray-500">There are not any accessibility issues!</p>
                            ) : (
                                transforms.map((transform, i) => <IssueCard transform={transform} element={elements[parseInt(transform.id)]} key={i}/>)
                            )}
                        </SidebarGroupContent>
                    </div>
                </SidebarGroup>
            </SidebarContent>
        </Sidebar>
    );
}
