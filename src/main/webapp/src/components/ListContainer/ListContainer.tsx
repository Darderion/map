import React, { FC, ReactNode } from "react";
import { Title } from "@mantine/core";
import './ListContainer.css'

interface ListPageProps {
    title: string;
    buttons: ReactNode[];
    search: ReactNode;
    list: ReactNode;
}

const ListPage: FC<ListPageProps> = ({ title, buttons, search, list }) => {
    return (
        <div className="pageContainer">
            <div className="listPage">
                <div className="listHead">
                    <Title className="listName" order={1}>{title}</Title>
                    <div className="listButtons">
                        {buttons.map((b: ReactNode) => b)}
                    </div>
                </div>
                <div className="search">
                    {search}
                </div>
                {list}
            </div>
        </div>
    )
}

export default ListPage;